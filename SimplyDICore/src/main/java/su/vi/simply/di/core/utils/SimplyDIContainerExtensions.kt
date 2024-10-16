package su.vi.simply.di.core.utils

import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.SimplyDIScope
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME

/**
 * Метод для получения зависимости из [SimplyDIScope].
 **/
@Suppress("DEPRECATION")
public inline fun <reified T : Any> SimplyDIContainer.addDependencyNow(
	scopeName: String = this.scopeName,
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
@Suppress("DEPRECATION")
public inline fun <reified T : Any> SimplyDIContainer.addDependencyLater(
	scopeName: String = this.scopeName,
	noinline factory: () -> T,
): Unit = addDependencyLater(
	scopeName = scopeName,
	clazz = T::class,
	factory = factory
)

@Suppress("DEPRECATION")
public inline fun <reified T : Any> SimplyDIContainer.replaceDependencyNow(
	scopeName: String = this.scopeName,
	noinline factory: () -> T,
): Unit = replaceDependencyNow(
	scopeName = scopeName,
	clazz = T::class,
	factory = factory
)

/**
 * Метод для получения зависимости из [SimplyDIScope].
 * @return T
 * @throws SimplyDINotFoundException если зависимость не будет найдена
 **/
@Suppress("DEPRECATION")
public inline fun <reified T : Any> SimplyDIContainer.replaceDependencyLater(
	scopeName: String = this.scopeName,
	noinline factory: () -> T,
): Unit = replaceDependencyLater(
	scopeName = scopeName,
	clazz = T::class,
	factory = factory
)

/**
 * Метод для получения зависимости из [SimplyDIScope].
 * @return T
 * @throws SimplyDINotFoundException если зависимость не будет найдена с добавлением в контейнер
 **/
@Suppress("DEPRECATION")
@Throws(SimplyDINotFoundException::class)
public inline fun <reified T> SimplyDIContainer.getDependency(
	scopeName: String = this.scopeName,
): T = getDependency(
	scopeName = scopeName,
	clazz = T::class
)

/**
 * Метод для получения зависимости из [SimplyDIScope].
 * @return T
 * @throws SimplyDINotFoundException если зависимость не будет найдена без добавления в контейнер
 **/
@Suppress("DEPRECATION")
@Throws(SimplyDINotFoundException::class)
public inline fun <reified T> SimplyDIContainer.getFactoryDependency(
	scopeName: String = this.scopeName,
): T = getFactoryDependency(
	scopeName = scopeName,
	clazz = T::class
)

/**
 * Метод для удаления зависимости из [SimplyDIScope] инициализированных и конструктор для инициализации.
 **/
@Suppress("DEPRECATION")
public inline fun <reified T : Any> SimplyDIContainer.deleteDependency(
	scopeName: String = this.scopeName,
): Unit = deleteDependency(
	clazz = T::class,
	scopeName = scopeName
)

/**
 * Метод для удаления зависимости из [SimplyDIScope] инициализированных и конструктор для инициализации.
 **/
@Suppress("DEPRECATION")
public inline fun <reified T : Any> SimplyDIContainer.depBenchmark(): Unit = depBenchmark<T>(
	clazz = T::class
)

@Suppress("DEPRECATION")
public fun SimplyDIContainer.addChainScopes(listOfScopes: List<String>) {
	addChainScopes(listOfScopes)
}

@Suppress("DEPRECATION")
public fun SimplyDIContainer.deleteChainedScopes(listOfScopes: List<String>) {
	deleteChainedScopes(listOfScopes)
}

@Suppress("DEPRECATION")
public fun SimplyDIContainer.initializeContainer(
	scopeName: String = this.scopeName,
	simplyLogLevel: SimplyLogLevel = SimplyLogLevel.EMPTY,
	isSearchInScope: Boolean = true,
) {
	initialize(
		scopeName = scopeName,
		simplyLogLevel = simplyLogLevel,
		isSearchInScope = isSearchInScope
	)
}