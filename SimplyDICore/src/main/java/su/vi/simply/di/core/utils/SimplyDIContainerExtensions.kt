package su.vi.simply.di.core.utils

import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.SimplyDIScope
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME

/**
 * Метод для получения зависимости из [SimplyDIScope].
 **/
public inline fun <reified T : Any> SimplyDIContainer.addDependencyNow(
	scopeName: String = DEFAULT_SCOPE_NAME,
	noinline factory: () -> T,
): Unit = addDependencyNow(
	scopeName = scopeName,
	clazz = T::class,
	factory = factory
)

/**
 * Метод для получения зависимости из [SimplyDIScope].
 * @return T
 * @throws SimplyDINotFoundException если зависимость не будет найдена
 **/
public inline fun <reified T : Any> SimplyDIContainer.addDependencyLater(
	scopeName: String = DEFAULT_SCOPE_NAME,
	noinline factory: () -> T,
): Unit = addDependencyLater(
	scopeName = scopeName,
	clazz = T::class,
	factory = factory
)

/**
 * Метод для получения зависимости из [SimplyDIScope].
 * @return T
 * @throws SimplyDINotFoundException если зависимость не будет найдена с добавлением в контейнер
 **/
@Throws(SimplyDINotFoundException::class)
public inline fun <reified T> SimplyDIContainer.getDependency(
	scopeName: String = DEFAULT_SCOPE_NAME,
): T = getDependency(
	scopeName = scopeName,
	clazz = T::class
)

/**
 * Метод для получения зависимости из [SimplyDIScope].
 * @return T
 * @throws SimplyDINotFoundException если зависимость не будет найдена без добавления в контейнер
 **/
@Throws(SimplyDINotFoundException::class)
public inline fun <reified T> SimplyDIContainer.getFactoryDependency(
	scopeName: String = DEFAULT_SCOPE_NAME,
): T = getFactoryDependency(
	scopeName = scopeName,
	clazz = T::class
)

/**
 * Метод для удаления зависимости из [SimplyDIScope] инициализированных и конструктор для инициализации.
 **/
public inline fun <reified T : Any> SimplyDIContainer.deleteDependency(
	scopeName: String = DEFAULT_SCOPE_NAME,
): Unit = deleteDependency(
	clazz = T::class,
	scopeName = scopeName
)

/**
 * Метод для удаления зависимости из [SimplyDIScope] инициализированных и конструктор для инициализации.
 **/
public inline fun <reified T : Any> SimplyDIContainer.depBenchmark(): Unit = depBenchmark<T> (
	clazz = T::class
)

public fun SimplyDIContainer.addChainScopes(listOfScopes: List<String>){
	addChainScopes(listOfScopes)
}

public fun SimplyDIContainer.deleteChainedScopes(listOfScopes: List<String>){
	deleteChainedScopes(listOfScopes)
}

public fun SimplyDIContainer.initialize(
	scopeName: String = DEFAULT_SCOPE_NAME,
	isSearchInScope: Boolean = true,
) {
	initialize(
		scopeName = scopeName,
		isSearchInScope = isSearchInScope
	)
}