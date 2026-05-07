# Bug Report — SimplyDI v1.0.9

## Отсутствие синхронизации в `getNullableDependency` приводит к дубликатам синглтонов при конкурентном доступе

---

## Среда

- **Библиотека:** `su.vi.simply.di:simply-di`
- **Версия:** `1.0.9`
- **Платформа:** Android (Kotlin)

---

## Краткое описание

`SimplyDIScope.getNullableDependency()` не синхронизирован и работает с `MutableMap` без атомарных операций. При конкурентном обращении двух потоков — один разрешает зависимость (вызывает фабрику), второй проходит мимо кэша и вызывает фабрику повторно — результат: **два разных экземпляра одной и той же зависимости**, зарегистрированной как синглтон через `addDependencyLater`.

Дополнительно: `delete()` некорректно очищает кэш (сравнивает `Map.Entry` с `KClass`) и `getFactoryDependency`/`getByClass` обходят кэш.

---

## Дефект 1: `getNullableDependency()` не потокобезопасен

**Файл:** `su/vi/simply/di/core/SimplyDIScope.kt`

### Код

```kotlin
// MutableMap — НЕ потокобезопасная коллекция
private val listOfDependencies: MutableMap<Any, Any> = mutableMapOf()

// Метод БЕЗ synchronized
internal fun <T : Any> getNullableDependency(kClass: KClass<*>): T? {
    val dependency = listOfDependencies[kClass] ?: run {
        val newInstance = initializerFactory[kClass]?.invoke()  // ← фабрика вызвана
            ?: return null
        listOfDependencies[kClass] = newInstance               // ← запись в кэш
        listOfDependencies[newInstance] = newInstance
        newInstance
    }
    return dependency
}
```

### Проблема

Отсутствует `synchronized` (или `ReentrantLock`) вокруг блока чтения-проверки-записи. При конкурентном доступе:

1. `listOfDependencies[kClass]` — чтение (null)
2. `initializerFactory[kClass]?.invoke()` — вызов фабрики
3. `listOfDependencies[kClass] = newInstance` — запись в кэш

Эти три шага **не атомарны**. Второй поток, запустившийся между шагами 1 и 3, пройдёт те же шаги и создаст **второй экземпляр**.

### Сценарий воспроизведения

```
Thread A: setPartnerProperty (main thread)
Thread B: BadgeElement (Compose UI thread)
```

| Шаг | Thread A | Thread B |
|-----|----------|----------|
| 1 | `getDependency<InitializePartnerPropertyUseCase>()` | `getDependency<BadgeGetDynamicUseCase>()` |
| 2 | Фабрика создаёт UseCase с `DILazyWrapper<UIRamStorageELK>` | Фабрика создаёт UseCase с `DILazyWrapper<UIRamStorageELK>` |
| 3 | `uiRamStorageELK().setPartnerProperty(...)` | `uiRamStorageELK().getPartnerProperty()` |
| 4 | `getNullableDependency` → `listOfDependencies[KClass]` — **null** | `getNullableDependency` → `listOfDependencies[KClass]` — **null** |
| 5 | **Фабрика вызвана** → `@810e3dc` | **Фабрика вызвана** → `@cc63ce5` |
| 6 | `listOfDependencies[KClass] = @810e3dc` | `listOfDependencies[KClass] = @cc63ce5` (перезаписан) |
| 7 | `setPartnerProperty` → URL установлен на `@810e3dc` | `getPartnerProperty` → читает с `@cc63ce5` → **null** |

### Ожидаемое поведение

`getNullableDependency` должен гарантировать, что при конкурентном доступе фабрика вызывается **один раз**. Второй поток должен **дождаться** завершения первого и получить тот же экземпляр.

### Фактическое поведение

Фабрика вызывается **дважды** — каждый поток создаёт свой экземпляр.

---

## Дефект 2: `delete()` не очищает `listOfDependencies`

**Файл:** `su/vi/simply/di/core/SimplyDIScope.kt`

### Код

```kotlin
internal fun delete(kClass: KClass<*>) {
    initializerFactory.remove(kClass)  // ← удаляет фабрику — OK

    listOfDependencies
        .filter { dependency -> dependency == kClass }   // ← BUG
        .forEach {
            listOfDependencies.remove(it)
        }
}
```

### Проблема

`listOfDependencies` — это `MutableMap<Any, Any>`. При итерации `.filter { dependency -> ... }` переменная `dependency` — это **`Map.Entry<Any, Any>`**, а не `KClass<*>`.

Сравнение `Map.Entry == KClass` **никогда не true**.

| Шаг | Что происходит | Результат |
|-----|----------------|-----------|
| 1 | `initializerFactory.remove(kClass)` | Фабрика удалена ✅ |
| 2 | `listOfDependencies.filter { entry == kClass }` | **Пустой список** — entry никогда не равен kClass ❌ |
| 3 | `forEach { remove(it) }` | **Ничего не удаляется** ❌ |

### Дополнительная проблема

В `getNullableDependency()` кэш заполняется двумя ключами:

```kotlin
listOfDependencies[kClass] = newInstance
listOfDependencies[newInstance] = newInstance   // ← второй ключ
```

После `delete()`:
- Фабрика удалена
- `listOfDependencies[KClass]` остался (неудалённый)
- `listOfDependencies[экземпляр]` остался (неудалённый)

При повторной регистрации (`addDependencyLater`) `isDependencyInScope()` вернёт `true` (найдёт старый экземпляр в `listOfDependencies`), и регистрация будет проигнорирована с лог-сообщением `REPLACE_ERR`.

### Ожидаемое поведение

`delete()` должен корректно удалять **и фабрику, и все закэшированные экземпляры**.

---

## Дефект 3: `getFactoryDependency` и `getByClass` обходят кэш

**Файл:** `su/vi/simply/di/core/SimplyDIScope.kt`

### Код

```kotlin
internal fun <T : Any> getFactoryDependency(kClass: KClass<*>): T {
    return initializerFactory[kClass]?.invoke() as? T  // ← всегда новый
        ?: throw SimplyDINotFoundException(...)
}

internal fun <T : Any> getByClass(kClass: KClass<*>): T? {
    return initializerFactory[kClass]?.invoke() as? T  // ← всегда новый
}
```

### Проблема

Эти методы **никогда не проверяют `listOfDependencies`** и **никогда не сохраняют результат**. Каждый вызов = новый экземпляр.

При этом публичные API (`SimplyDIContainer.getFactoryDependency()`, `SimplyDIContainer.getByClassAnyway()`) используют именно эти методы:

```kotlin
// SimplyDIContainer.kt
internal fun <T : Any> getByClassAnyway(scopeName: String, kClass: KClass<*>): T {
    val scope = mapContainers[scopeName]
    return scope.getByClass(kClass)  // ← всегда новый
        ?: mapContainers.asSequence()...
}
```

### Ожидаемое поведение

- `getFactoryDependency()` — документировать как «всегда создаёт новый экземпляр» (это его предназначение)
- `getByClassAnyway()` — использовать `getNullableDependency()` (через кэш) вместо `getByClass()`

---

## Как воспроизвести (полный тест)

### Сценарий A: Конкурентный доступ

```kotlin
@Test
fun testConcurrentDependencyResolution() {
    val container = SimplyDIContainer.initialize(scopeName = "test")
    container.addDependencyLater<MyService> { MyServiceImpl() }

    val instances = mutableListOf<MyService>()
    val barrier = CyclicBarrier(2)

    runBlocking {
        coroutineScope {
            launch {
                barrier.await()
                instances += container.getDependency<MyService>()
            }
            launch {
                barrier.await()
                instances += container.getDependency<MyService>()
            }
        }
    }

    // Ожидание: 1 экземпляр
    // Факт: 2 экземпляра (фабрика вызвана дважды)
    assert(instances.size == 1) // FAIL
}
```

### Сценарий B: `delete()` + `addDependencyLater()` = устаревший экземпляр

```kotlin
@Test
fun testDeleteDoesNotClearCache() {
    val container = SimplyDIContainer.initialize(scopeName = "test2")

    container.addDependencyLater<MyService> { MyServiceImpl() }
    val first = container.getDependency<MyService>()

    container.deleteDependency<MyService>()
    container.addDependencyLater<MyService> { MyServiceImpl() }

    val second = container.getDependency<MyService>()

    // Ожидание: second != first (новая фабрика → новый экземпляр)
    // Факт: second == first (старый экземпляр остался в кэше)
    assert(first !== second) // FAIL
}
```

---

## Предложенное исправление

### Фикс дефекта 1 (`getNullableDependency`)

```kotlin
internal fun <T : Any> getNullableDependency(kClass: KClass<*>): T? = synchronized(this) {
    listOfDependencies[kClass] ?: run {
        val newInstance = initializerFactory[kClass]?.invoke()
            ?: return null
        listOfDependencies[kClass] = newInstance
        listOfDependencies[newInstance] = newInstance
        newInstance
    }
} as? T
```

Или более гранулярная синхронизация:

```kotlin
private val lock = ReentrantLock()

internal fun <T : Any> getNullableDependency(kClass: KClass<*>): T? {
    lock.lock()
    try {
        return listOfDependencies[kClass] ?: run {
            val newInstance = initializerFactory[kClass]?.invoke()
                ?: return null
            listOfDependencies[kClass] = newInstance
            listOfDependencies[newInstance] = newInstance
            newInstance
        } as? T
    } finally {
        lock.unlock()
    }
}
```

### Фикс дефекта 2 (`delete`)

```kotlin
internal fun delete(kClass: KClass<*>) {
    initializerFactory.remove(kClass)

    // Удаляем по ключу-классу
    listOfDependencies.remove(kClass)?.let { instance ->
        // Удаляем и обратную ссылку (instance -> instance)
        listOfDependencies.remove(instance)
    }
}
```

Или более безопасный вариант:

```kotlin
internal fun delete(kClass: KClass<*>) {
    initializerFactory.remove(kClass)
    listOfDependencies.filterKeys { it == kClass }.keys
        .forEach { key ->
            val instance = listOfDependencies.remove(key)
            instance?.let { listOfDependencies.remove(it) }
        }
}
```

### Фикс дефекта 3 (`getByClassAnyway`)

```kotlin
internal fun <T : Any> getByClassAnyway(scopeName: String, kClass: KClass<*>): T {
    val scope = mapContainers[scopeName] ?: throw SimplyDINotFoundException(...)

    return scope.getNullableDependency(kClass)   // ← сначала через кэш
        ?: mapContainers.asSequence()
            .filter { entry -> entry.value.isSearchInScope }
            .mapNotNull { it.value.getNullableDependency(kClass) }  // ← тоже через кэш
            .firstOrNull() as? T
        ?: findInChainScopes(scopeName, kClass)
        ?: throw SimplyDINotFoundException(...)
}
```

---

## Влияние

Без синхронизации в `getNullableDependency` **любой конкурентный доступ** к зависимости может создать дубликаты синглтонов. Это приводит к:

- **Разные экземпляры** одного и того же «синглтона» в разных потоках
- **Пустые/устаревшие данные** — данные записаны в один экземпляр, читаются из другого
- **Неразрешимые race condition** — время от времени, при определённой раскладке потоков
- **Невозможность воспроизвести в тестах** — проблема проявляется только при реальных задержках между потоками

В нашем случае это проявляется при:
1. Thread A (main): `setPartnerProperty` — записывает URL в экземпляр #1
2. Thread B (Compose UI): `BadgeElement` → `getPartnerProperty` — читает из экземпляра #2 → **null**
