@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package su.vi.simply.di.core.entry_point

import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.lazy.SimplyDILazyWrapper
import su.vi.simply.di.core.utils.SimplyLogLevel
import kotlin.reflect.KClass

/**
 * Use it to instantly add a dependency.
 * @param scopeName name of your scope.
 * @param factory with your dependency.
 **/
public inline fun <reified T : Any> SimplyDIContainer.addDependencyNow(
	scopeName: String = this.scopeName,
	noinline factory: () -> T,
): Unit = addDependencyNow(
	scopeName = scopeName,
	clazz = T::class,
	factory = factory
)

/**
 * Use it to instantly add a dependency.
 * @param scopeName name of your scope.
 * @param clazz KClass of your dependency.
 * @param factory with your dependency.
 **/
public fun <T : Any> SimplyDIContainer.addDependencyNow(
	scopeName: String = this.scopeName,
	clazz: KClass<*>,
	factory: () -> T,
): Unit = addDependencyNow(
	scopeName = scopeName,
	kClass = clazz,
	factory = factory
)

/**
 * Use it to add a dependency by lazy.
 * @param scopeName name of your scope.
 * @param factory with your dependency.
 **/
public inline fun <reified T : Any> SimplyDIContainer.addDependencyLater(
	scopeName: String = this.scopeName,
	noinline factory: () -> T,
): Unit = addDependencyLater(
	scopeName = scopeName,
	clazz = T::class,
	factory = factory
)

/**
 * Use it to add a dependency by lazy.
 * @param scopeName name of your scope.
 * @param clazz KClass of your dependency.
 * @param factory with your dependency.
 **/
public fun <T : Any> SimplyDIContainer.addDependencyLater(
	scopeName: String = this.scopeName,
	clazz: KClass<*>,
	factory: () -> T,
): Unit = addDependencyLater(
	scopeName = scopeName,
	kClass = clazz,
	factory = factory
)

/**
 * Use it to replace a dependency.
 * @param scopeName name of your scope.
 * @param factory with your dependency.
 **/
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
 * @param clazz KClass of your dependency.
 * @param factory with your dependency.
 **/
public fun <T : Any> SimplyDIContainer.replaceDependencyNow(
	scopeName: String = this.scopeName,
	clazz: KClass<*>,
	factory: () -> T,
): Unit = replaceDependencyNow(
	scopeName = scopeName,
	kClass = clazz,
	factory = factory
)

/**
 * Use it to replace a dependency.
 * @param scopeName name of your scope.
 * @param factory with your dependency.
 **/
public inline fun <reified T : Any> SimplyDIContainer.replaceDependencyLater(
	scopeName: String = this.scopeName,
	noinline factory: () -> T,
): Unit = replaceDependencyLater(
	scopeName = scopeName,
	clazz = T::class,
	factory = factory
)

/**
 * Use it to replace a dependency.
 * @param scopeName name of your scope.
 * @param clazz KClass of your dependency.
 * @param factory with your dependency.
 **/
public fun <T : Any> SimplyDIContainer.replaceDependencyLater(
	scopeName: String = this.scopeName,
	clazz: KClass<*>,
	factory: () -> T,
): Unit = replaceDependencyLater(
	scopeName = scopeName,
	kClass = clazz,
	factory = factory
)

/**
 * Use it to get the dependency.
 * @param scopeName name of your scope.
 * @throws SimplyDINotFoundException if the dependency not created you receive.
 **/
@Throws(SimplyDINotFoundException::class)
public inline fun <reified T> SimplyDIContainer.getDependency(
	scopeName: String = this.scopeName,
): T = getDependency(
	scopeName = scopeName,
	clazz = T::class
)

/**
 * Use it to get the dependency.
 * @param scopeName name of your scope.
 * @param clazz KClass of your dependency.
 * @throws SimplyDINotFoundException if the dependency not created you receive.
 **/
@Throws(SimplyDINotFoundException::class)
public fun <T> SimplyDIContainer.getDependency(
	scopeName: String = this.scopeName,
	clazz: KClass<*>,
): T = getDependency(
	scopeName = scopeName,
	kClass = clazz
)

/**
 * Use it to get a dependency from you scope or chained scopes each time new.
 * @param scopeName name of your scope.
 * @throws SimplyDINotFoundException if the dependency not created you receive.
 **/
@Throws(SimplyDINotFoundException::class)
public inline fun <reified T> SimplyDIContainer.getByClassAnyway(
	scopeName: String = this.scopeName,
): T = getByClassAnyway(
	scopeName = scopeName,
	clazz = T::class
)

/**
 * Use it to get a dependency from you scope or chained scopes each time new.
 * @param scopeName name of your scope.\
 * @param clazz KClass of your dependency.
 * @throws SimplyDINotFoundException if the dependency not created you receive.
 **/
@Throws(SimplyDINotFoundException::class)
public fun <T> SimplyDIContainer.getByClassAnyway(
	scopeName: String = this.scopeName,
	clazz: KClass<*>,
): T = getByClassAnyway(
	scopeName = scopeName,
	kClass = clazz
)

/**
 * Use it to get the dependency by lazy.
 * @param scopeName name of your scope.
 * @throws SimplyDINotFoundException if the dependency not created you receive.
 **/
@Throws(SimplyDINotFoundException::class)
public inline fun <reified T : Any> SimplyDIContainer.getDependencyByLazy(
	scopeName: String = this.scopeName,
): SimplyDILazyWrapper<T> = getDependencyByLazy(
	scopeName = scopeName,
	clazz = this::class
)

/**
 * Use it to get the dependency by lazy.
 * @param scopeName name of your scope.
 * @param clazz KClass of your dependency.
 * @throws SimplyDINotFoundException if the dependency not created you receive.
 **/
@Throws(SimplyDINotFoundException::class)
public fun <T : Any> SimplyDIContainer.getDependencyByLazy(
	scopeName: String = this.scopeName,
	clazz: KClass<*>,
): SimplyDILazyWrapper<T> = getDependencyByLazy(
	scopeName = scopeName,
	kClass = clazz
)

/**
 * Use it to get new dependency every call.
 * @param scopeName name of your scope.
 * @throws SimplyDINotFoundException if the dependency not created you receive.
 **/
@Throws(SimplyDINotFoundException::class)
public inline fun <reified T> SimplyDIContainer.getFactoryDependency(
	scopeName: String = this.scopeName,
): T = getFactoryDependency(
	scopeName = scopeName,
	clazz = T::class
)

/**
 * Use it to get new dependency every call.
 * @param scopeName name of your scope.
 * @param clazz KClass of your dependency.
 * @throws SimplyDINotFoundException if the dependency not created you receive.
 **/
@Throws(SimplyDINotFoundException::class)
public fun <T : Any> SimplyDIContainer.getFactoryDependency(
	scopeName: String = this.scopeName,
	clazz: KClass<*>,
): T = getFactoryDependency(
	scopeName = scopeName,
	kClass = clazz
)

/**
 * Use it to delete the dependency from scope.
 * @param scopeName name of your scope.
 **/
public inline fun <reified T : Any> SimplyDIContainer.deleteDependency(
	scopeName: String = this.scopeName,
): Unit = deleteDependency(
	clazz = T::class,
	scopeName = scopeName
)

/**
 * Use it to delete the dependency from scope.
 * @param scopeName name of your scope.
 * @param clazz KClass of your dependency.
 **/
public fun SimplyDIContainer.deleteDependency(
	scopeName: String = this.scopeName,
	clazz: KClass<*>,
): Unit = deleteDependency(
	kClass = clazz,
	scopeName = scopeName
)

/**
 * Use it to check speed of call a dependency.
 **/
public inline fun <reified T : Any> SimplyDIContainer.depBenchmark(): Unit = depBenchmark<T>(
	clazz = T::class
)

/**
 * Use it to check speed of call a dependency.
 * @param clazz KClass of your dependency.
 **/
public fun <T : Any> SimplyDIContainer.depBenchmark(
	clazz: KClass<*>,
): Unit = depBenchmark<T>(
	kClass = clazz
)

/**
 * Use it to bind scopes.
 * @param listOfScopes List of names of scopes.
 **/
public fun SimplyDIContainer.addChainScopes(listOfScopes: List<String>) {
	addChainScopes(listOfScopes)
}

/**
 * Use it to delete bind of scopes.
 * @param listOfScopes List of names of scopes.
 **/
public fun SimplyDIContainer.deleteChainedScopes(listOfScopes: List<String>) {
	deleteChainedScopes(listOfScopes)
}

/**
 * Initialize method for new container.
 * @param scopeName name of the new container.
 * @param simplyLogLevel where you can set level of logs for you needs for example for release
 * would do use [su.vi.simply.di.core.utils.SimplyLogLevel.EMPTY] for debug [su.vi.simply.di.core.utils.SimplyLogLevel.FULL].
 * @param isSearchInScope if you want to use this container like data store or you need
 * to share dependencies from this container you would set value like true or you can bind them [SimplyDIContainer.addChainScopes] .
 */
public fun SimplyDIContainer.initializeContainer(
	scopeName: String = this.scopeName,
	simplyLogLevel: SimplyLogLevel = SimplyLogLevel.EMPTY,
	isSearchInScope: Boolean = this.isSearchInScope,
) {
	initialize(
		scopeName = scopeName,
		simplyLogLevel = simplyLogLevel,
		isSearchInScope = isSearchInScope
	)
}