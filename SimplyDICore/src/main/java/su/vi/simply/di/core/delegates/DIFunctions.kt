package su.vi.simply.di.core.delegates

import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import kotlin.reflect.KClass

/**
 * Делегат для ленивого получения зависимостей из [SimplyDIContainer] в любом месте, если она в нём есть.
 *
 * @param mode режим ленивого получения [LazyThreadSafetyMode], рекомендуется использовать мод по умолчанию.
 * @param T инстанс класса.
 *
 * @throws SimplyDINotFoundException если зависимость не будет найдена с добавлением в контейнер
 */
@Throws(SimplyDINotFoundException::class)
public inline fun <reified T : Any> inject(
    scopeName: String = DEFAULT_SCOPE_NAME,
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
): Lazy<T> = lazy(mode) {
    SimplyDIContainer.instance.getDependency(
        clazz = T::class,
        scopeName = scopeName
    )
}

