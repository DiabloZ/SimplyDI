@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package su.vi.kdi.core.entry_point

import su.vi.kdi.Lifecycle
import su.vi.kdi.core.KDIContainer
import su.vi.kdi.core.error.KDINotFoundException
import su.vi.kdi.core.lazy.KDILazyWrapper
import su.vi.kdi.core.utils.KDILogLevel
import kotlin.reflect.KClass

/**
 * Use it to add a dependency by lazy.
 * @param scopeName name of your scope.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 * @param lifecycle of your dependency, for example [Lifecycle.SINGLETON] or [Lifecycle.FACTORY].
 * @param factory with your dependency.
 **/
public inline fun <reified T : Any> KDIContainer.addDependency(
	scopeName: String = this.scopeName,
	name: String? = null,
	lifecycle: Lifecycle = Lifecycle.SINGLETON,
	noinline factory: () -> T,
): Unit = addDependency(
	scopeName = scopeName,
	clazz = T::class,
	name = name,
	lifecycle = lifecycle,
	factory = factory
)

/**
 * Use it to add a dependency by lazy.
 * @param scopeName name of your scope.
 * @param clazz KClass of your dependency.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 * @param lifecycle of your dependency, for example [Lifecycle.SINGLETON] or [Lifecycle.FACTORY].
 * @param factory with your dependency.
 **/
public fun <T : Any> KDIContainer.addDependency(
	scopeName: String = this.scopeName,
	clazz: KClass<*>,
	name: String? = null,
	lifecycle: Lifecycle = Lifecycle.SINGLETON,
	factory: () -> T,
): Unit = addDependency(
	scopeName = scopeName,
	kClass = clazz,
	name = name,
	lifecycle = lifecycle,
	factory = factory
)

/**
 * Use it to add a dependency by lazy.
 * @param clazz KClass of your dependency.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 * @param lifecycle of your dependency, for example [Lifecycle.SINGLETON] or [Lifecycle.FACTORY].
 * @param factory with your dependency.
 **/
public fun <T : Any> KDIContainer.addDependency(
	clazz: KClass<*>,
	name: String? = null,
	lifecycle: Lifecycle = Lifecycle.SINGLETON,
	factory: () -> T,
): Unit = addDependency(
	scopeName = scopeName,
	kClass = clazz,
	name = name,
	lifecycle = lifecycle,
	factory = factory
)

/**
 * Use it to instantly add a dependency without lambda functions, less weight.
 * @param scopeName name of your scope.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 **/
public inline fun <reified T : Any> KDIContainer.addDependencyLambdaAuto(
	scopeName: String = this.scopeName,
	name: String? = null,
): Unit = addDependencyLambdaAuto(
	scopeName = scopeName,
	clazz = T::class,
	name = name,
)

/**
 * Use it to instantly add a dependency without lambda functions, less weight.
 * @param scopeName name of your scope.
 * @param clazz KClass of your dependency.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 **/
public fun <T : Any> KDIContainer.addDependencyLambdaAuto(
	scopeName: String = this.scopeName,
	clazz: KClass<T>,
	name: String? = null,
): Unit = addDependencyLambdaAuto(
	scopeName = scopeName,
	kClass = clazz,
	name = name,
)

/**
 * Use it to instantly add a dependency without lambda functions, less weight.
 * @param clazz KClass of your dependency.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 **/
public fun <T : Any> KDIContainer.addDependencyLambdaAuto(
	clazz: KClass<T>,
	name: String? = null,
): Unit = addDependencyLambdaAuto(
	scopeName = scopeName,
	kClass = clazz,
	name = name,
)

/**
 * Use it to instantly add a dependency without lambda functions, less weight.
 * @param scopeName name of your scope.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 **/
public inline fun <reified T : Any> KDIContainer.addDependencyAuto(
	scopeName: String = this.scopeName,
	name: String? = null,
): Unit = addDependencyAuto(
	scopeName = scopeName,
	clazz = T::class,
	name = name,
)

/**
 * Use it to instantly add a dependency without lambda functions, less weight.
 * @param scopeName name of your scope.
 * @param clazz KClass of your dependency.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 **/
public fun <T : Any> KDIContainer.addDependencyAuto(
	scopeName: String = this.scopeName,
	clazz: KClass<T>,
	name: String? = null,
): Unit = addDependencyAuto(
	scopeName = scopeName,
	kClass = clazz,
	name = name,
)

/**
 * Use it to instantly add a dependency without lambda functions, less weight.
 * @param clazz KClass of your dependency.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 **/
public fun <T : Any> KDIContainer.addDependencyAuto(
	clazz: KClass<T>,
	name: String? = null,
): Unit = addDependencyAuto(
	scopeName = scopeName,
	kClass = clazz,
	name = name,
)


/**
 * Use it to instantly add a dependency without lambda functions, less weight.
 * @param scopeName name of your scope.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 **/
public inline fun <reified T : Any> KDIContainer.addDependencyManually(
    scopeName: String = this.scopeName,
    name: String? = null,
    supertypes: List<KClass<*>>,
): Unit = addDependencyManually(
    scopeName = scopeName,
    clazz = T::class,
    name = name,
    supertypes = supertypes
)

/**
 * Use it to instantly add a dependency without lambda functions, less weight.
 * @param scopeName name of your scope.
 * @param clazz KClass of your dependency.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 **/
public fun <T : Any> KDIContainer.addDependencyManually(
    scopeName: String = this.scopeName,
    clazz: KClass<T>,
    name: String? = null,
    supertypes: List<KClass<*>>,
): Unit = addDependencyManually(
    scopeName = scopeName,
    kClass = clazz,
    name = name,
    supertypes = supertypes
)

/**
 * Use it to instantly add a dependency without lambda functions, less weight.
 * @param clazz KClass of your dependency.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 **/
public fun <T : Any> KDIContainer.addDependencyManually(
    clazz: KClass<T>,
    name: String? = null,
    supertypes: List<KClass<*>>,
): Unit = addDependencyManually(
    scopeName = scopeName,
    kClass = clazz,
    name = name,
    supertypes = supertypes
)


/**
 * Use it to get the dependency.
 * @param scopeName name of your scope.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 * @throws KDINotFoundException if the dependency not created you receive.
 **/
@Throws(KDINotFoundException::class)
public inline fun <reified T: Any> KDIContainer.getDependency(
	scopeName: String = this.scopeName,
	name: String? = null,
): T = getDependency(
	scopeName = scopeName,
	name = name,
	clazz = T::class
)

/**
 * Use it to get the dependency.
 * @param scopeName name of your scope.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 * @param clazz KClass of your dependency.
 * @throws KDINotFoundException if the dependency not created you receive.
 **/
@Throws(KDINotFoundException::class)
public fun <T: Any> KDIContainer.getDependency(
	scopeName: String = this.scopeName,
	name: String? = null,
	clazz: KClass<T>,
): T = getDependency(
	scopeName = scopeName,
	name = name,
	kClass = clazz
)

/**
 * Use it to get the dependency.
 * @param clazz KClass of your dependency.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 * @throws KDINotFoundException if the dependency not created you receive.
 **/
@Throws(KDINotFoundException::class)
public fun <T: Any> KDIContainer.getDependency(
	clazz: KClass<T>,
	name: String? = null,
): T = getDependency<T>(
	scopeName = scopeName,
	name = name,
	kClass = clazz
)

/**
 * Use it to get a dependency from you scope or chained scopes each time new.
 * @param scopeName name of your scope.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 * @throws KDINotFoundException if the dependency not created you receive.
 **/
@Throws(KDINotFoundException::class)
public inline fun <reified T: Any> KDIContainer.getByClassAnyway(
	scopeName: String = this.scopeName,
	name: String? = null,
): T = getByClassAnyway(
	scopeName = scopeName,
	name = name,
	clazz = T::class,
)

/**
 * Use it to get a dependency from you scope or chained scopes each time new.
 * @param scopeName name of your scope.
 * @param clazz KClass of your dependency.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 * @throws KDINotFoundException if the dependency not created you receive.
 **/
@Throws(KDINotFoundException::class)
public fun <T: Any> KDIContainer.getByClassAnyway(
	scopeName: String = this.scopeName,
	clazz: KClass<T>,
	name: String? = null,
): T = getByClassAnyway(
	scopeName = scopeName,
	name = name,
	kClass = clazz
)

/**
 * Use it to get a dependency from you scope or chained scopes each time new.
 * @param clazz KClass of your dependency.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 * @throws KDINotFoundException if the dependency not created you receive.
 **/
@Throws(KDINotFoundException::class)
public fun <T: Any> KDIContainer.getByClassAnyway(
	clazz: KClass<T>,
	name: String? = null,
): T = getByClassAnyway(
	scopeName = scopeName,
	name = name,
	kClass = clazz,
)

/**
 * Use it to get the dependency by lazy.
 * @param scopeName name of your scope.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 * @throws KDINotFoundException if the dependency not created you receive.
 **/
@Throws(KDINotFoundException::class)
public inline fun <reified T : Any> KDIContainer.getDependencyByLazy(
	scopeName: String = this.scopeName,
	name: String? = null,
): KDILazyWrapper<T> = getDependencyByLazy<T>(
	scopeName = scopeName,
	name = name,
	clazz = T::class,
)

/**
 * Use it to get the dependency by lazy.
 * @param scopeName name of your scope.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 * @param clazz KClass of your dependency.
 * @throws KDINotFoundException if the dependency not created you receive.
 **/
@Throws(KDINotFoundException::class)
public fun <T : Any> KDIContainer.getDependencyByLazy(
	scopeName: String = this.scopeName,
	name: String? = null,
	clazz: KClass<T>,
): KDILazyWrapper<T> = getDependencyByLazy<T>(
	scopeName = scopeName,
	name = name,
	kClass = clazz
)

/**
 * Use it to get the dependency by lazy.
 * @param name name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
 * @param clazz KClass of your dependency.
 * @throws KDINotFoundException if the dependency not created you receive.
 **/
@Throws(KDINotFoundException::class)
public fun <T : Any> KDIContainer.getDependencyByLazy(
	name: String? = null,
	clazz: KClass<T>,
): KDILazyWrapper<T> = getDependencyByLazy<T>(
	scopeName = scopeName,
	name = name,
	kClass = clazz
)

/**
 * Use it to bind scopes.
 * @param listOfScopes List of names of scopes.
 **/
public fun KDIContainer.addChainScopes(listOfScopes: List<String>) {
	addChainScopes(listOfScopes)
}

/**
 * Use it to delete bind of scopes.
 * @param listOfScopes List of names of scopes.
 **/
public fun KDIContainer.deleteChainedScopes(listOfScopes: List<String>) {
	deleteChainedScopes(listOfScopes)
}

/**
 * Initialize method for new container.
 * @param scopeName name of the new container.
 * @param kdiLogLevel where you can set level of logs for you needs for example for release
 * would do use [KDILogLevel.Empty] for debug [KDILogLevel.Full].
 * @param isSearchInScope if you want to use this container like data store or you need
 * to share dependencies from this container you would set value like true or you can bind them [KDIContainer.addChainScopes] .
 */
public fun KDIContainer.initializeContainer(
	scopeName: String = this.scopeName,
	kdiLogLevel: KDILogLevel = KDILogLevel.Empty,
	isSearchInScope: Boolean = this.isSearchInScope,
	kdiDSL: KDIContainerDSL? = null,
) {
	initialize(
		scopeName = scopeName,
		kdiLogLevel = kdiLogLevel,
		isSearchInScope = isSearchInScope,
		dslBuilder = kdiDSL
	)
}