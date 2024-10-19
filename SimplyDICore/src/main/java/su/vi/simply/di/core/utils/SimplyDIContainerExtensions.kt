package su.vi.simply.di.core.utils

import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.SimplyDIScope
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME

/**
 * Use it to instantly add a dependency.
 * @param scopeName name of your scope.
 * @param factory with your dependency.
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
 * Use it to add a dependency by lazy.
 * @param scopeName name of your scope.
 * @param factory with your dependency.
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

/**
 * Use it to replace a dependency.
 * @param scopeName name of your scope.
 * @param factory with your dependency.
 **/
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
 * Use it to replace a dependency.
 * @param scopeName name of your scope.
 * @param factory with your dependency.
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
 * Use it to get the dependency.
 * @param scopeName name of your scope.
 * @throws SimplyDINotFoundException if the dependency not created you receive.
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
 * Use it to get new dependency every call.
 * @param scopeName name of your scope.
 * @throws SimplyDINotFoundException if the dependency not created you receive.
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
 * Use it to delete the dependency from scope.
 * @param scopeName name of your scope.
 **/
@Suppress("DEPRECATION")
public inline fun <reified T : Any> SimplyDIContainer.deleteDependency(
	scopeName: String = this.scopeName,
): Unit = deleteDependency(
	clazz = T::class,
	scopeName = scopeName
)

/**
 * Use it to check speed of call a dependency.
 * @param scopeName name of your scope.
 **/
@Suppress("DEPRECATION")
public inline fun <reified T : Any> SimplyDIContainer.depBenchmark(): Unit = depBenchmark<T>(
	clazz = T::class
)

/**
 * Use it to bind scopes.
 * @param listOfScopes List of names of scopes.
 **/
@Suppress("DEPRECATION")
public fun SimplyDIContainer.addChainScopes(listOfScopes: List<String>) {
	addChainScopes(listOfScopes)
}

/**
 * Use it to delete bind of scopes.
 * @param listOfScopes List of names of scopes.
 **/
@Suppress("DEPRECATION")
public fun SimplyDIContainer.deleteChainedScopes(listOfScopes: List<String>) {
	deleteChainedScopes(listOfScopes)
}

/**
 * Initialize method for new container.
 * @param scopeName name of the new container.
 * @param simplyLogLevel where you can set level of logs for you needs for example for release
 * would do use [SimplyLogLevel.EMPTY] for debug [SimplyLogLevel.FULL].
 * @param isSearchInScope if you want to use this container like data store or you need
 * to share dependencies from this container you would set value like true or you can bind them [SimplyDIContainer.addChainScopes] .
 */
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