package su.vi.kdi.core.entry_point

import su.vi.kdi.Lifecycle
import su.vi.kdi.core.KDIContainer
import su.vi.kdi.core.error.KDINotFoundException
import su.vi.kdi.core.lazy.KDILazyWrapper
import su.vi.kdi.core.utils.KDIConstants.DEFAULT_SCOPE_NAME
import su.vi.kdi.core.utils.KDILogLevel

/**
 * DSL to create [KDIContainer]
 */
public class KDIContainerDSL {
	public lateinit var scopeName: String
	private var isSearchInScope: Boolean = true
	internal lateinit var builder: KDIContainerDSL.() -> Unit

	public val mKDIContainer: KDIContainer by lazy {
		KDIContainer(
			scopeName = scopeName,
			isSearchInScope = isSearchInScope
		)
	}

	/**
	 * Initialize method for new container.
	 * @param scopeName name of the new container.
	 * @param kdiLogLevel where you can set level of logs for you needs for example for release
	 * would do use [su.vi.kdi.core.utils.KDILogLevel.Empty] for debug [su.vi.kdi.core.utils.KDILogLevel.Full].
	 * @param isSearchInScope if you want to use this container like data store or you need
	 * to share dependencies from this container you would set value like true or you can bind them [KDIContainer.addChainScopes].
	 */
	internal fun initialize(
		scopeName: String = DEFAULT_SCOPE_NAME,
		kdiLogLevel: KDILogLevel = KDILogLevel.Empty,
		isSearchInScope: Boolean = true,
		builder: KDIContainerDSL.() -> Unit,
	): KDIContainer {
		this.scopeName = scopeName
		mKDIContainer.initializeContainer(
			scopeName = scopeName,
			kdiLogLevel = kdiLogLevel,
			isSearchInScope = isSearchInScope,
			kdiDSL = this
		)
		this.builder = builder
		return mKDIContainer
	}

	/**
	 * Use it to instantly add a dependency.
	 * @param lifecycle of your dependency, for example [Lifecycle.SINGLETON] or [Lifecycle.FACTORY].
	 * @param name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
	 * @param factory with your dependency.
	 **/
	public inline fun <reified T : Any> addDependency(
		lifecycle: Lifecycle = Lifecycle.SINGLETON,
		name: String? = null,
		noinline factory: () -> T,
	): Unit = mKDIContainer.addDependency<T>(
		scopeName = scopeName,
		name = name,
		lifecycle = lifecycle,
		clazz = T::class,
		factory = factory
	)

	/**
	 * Use it to instantly add a dependency without lambda functions, less weight.
	 * @param name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
	 **/
	public inline fun <reified T : Any> addDependencyAuto(
		name: String? = null,
	): Unit = mKDIContainer.addDependencyAuto<T>(
		scopeName = scopeName,
		name = name,
		clazz = T::class,
	)



	/**
	 * Use it to get the dependency.
	 * @param name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
	 * @throws KDINotFoundException if the dependency not created you receive.
	 **/
	@Throws(KDINotFoundException::class)
	public inline fun <reified T: Any> getDependency(name: String? = null): T = mKDIContainer.getDependency<T>(
		scopeName = scopeName,
		name = name,
		clazz = T::class
	)

	/**
	 * Use it to get the dependency.
	 * @param name of your dependency, if you want to use it like [KDIContainer.getDependency] with name.
	 * @throws KDINotFoundException if the dependency not created you receive.
	 **/
	@Throws(KDINotFoundException::class)
	public inline fun <reified T : Any> getDependencyByLazy(
		name: String? = null
	): KDILazyWrapper<T> =
		mKDIContainer.getDependencyByLazy(
			scopeName = scopeName,
			clazz = T::class
		)

	/**
	 * Use it to bind scopes.
	 * @param listOfScopes List of names of scopes.
	 **/
	public fun addChainScopes(listOfScopes: List<String>): Unit = mKDIContainer.addChainScopes(
		listOfScopes = listOfScopes
	)

	/**
	 * Use it to delete bind of scopes.
	 * @param listOfScopes List of names of scopes.
	 **/
	public fun deleteChainedScopes(listOfScopes: List<String>): Unit = mKDIContainer.deleteChainedScopes(
		listOfScopes = listOfScopes
	)
}

/**
 * Initialize method for new container.
 * @param scopeName name of the new container.
 * @param kdiLogLevel where you can set level of logs for you needs for example for release
 * would do use [KDILogLevel.Empty] for debug [KDILogLevel.Full].
 * @param isSearchInScope if you want to use this container like data store or you need
 * to share dependencies from this container you would set value like true or you can bind them [KDIContainerDSL.addChainScopes].
 * @param builder context of [KDIContainerDSL] where you can interact with [KDIContainer] more convenient.
 */
public fun KDIContainer.Companion.initialize(
	scopeName: String = DEFAULT_SCOPE_NAME,
	kdiLogLevel: KDILogLevel = KDILogLevel.Empty,
	isSearchInScope: Boolean = true,
	builder: KDIContainerDSL.() -> Unit,
): KDIContainer {
	val containerBuilder = KDIContainerDSL()

	return containerBuilder.initialize(
		scopeName = scopeName,
		kdiLogLevel = kdiLogLevel,
		isSearchInScope = isSearchInScope,
		builder = builder
	)
}
