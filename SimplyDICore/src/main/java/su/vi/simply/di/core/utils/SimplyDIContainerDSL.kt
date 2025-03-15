package su.vi.simply.di.core.utils

import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.lazy.SimplyDILazyWrapper
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import kotlin.reflect.KClass

/**
 * DSL to create [SimplyDIContainer]
 */
public class SimplyDIContainerDSL {
	public lateinit var scopeName: String
	private var isSearchInScope: Boolean = true

	public val simplyDIContainer: SimplyDIContainer by lazy {
		SimplyDIContainer(
			scopeName = scopeName,
			isSearchInScope = isSearchInScope
		)
	}

	/**
	 * Initialize method for new container.
	 * @param scopeName name of the new container.
	 * @param simplyLogLevel where you can set level of logs for you needs for example for release
	 * would do use [SimplyLogLevel.EMPTY] for debug [SimplyLogLevel.FULL].
	 * @param isSearchInScope if you want to use this container like data store or you need
	 * to share dependencies from this container you would set value like true or you can bind them [SimplyDIContainer.addChainScopes].
	 */
	internal fun initialize(
		scopeName: String = DEFAULT_SCOPE_NAME,
		simplyLogLevel: SimplyLogLevel = SimplyLogLevel.EMPTY,
		isSearchInScope: Boolean = true,
		context: SimplyDIContainerDSL.() -> Unit,
	): SimplyDIContainer {
		this.scopeName = scopeName
		simplyDIContainer.initializeContainer(
			scopeName,
			simplyLogLevel,
			isSearchInScope
		)
		context.invoke(this)
		return simplyDIContainer
	}

	/**
	 * Use it to instantly add a dependency.
	 * @param factory with your dependency.
	 **/
	public inline fun <reified T : Any> addDependencyNow(
		noinline factory: () -> T,
	): Unit = simplyDIContainer.addDependencyNow<T>(
		scopeName = scopeName,
		factory = factory
	)

	/**
	 * Use it to add a dependency by lazy.
	 * @param factory with your dependency.
	 **/
	public inline fun <reified T : Any> addDependencyLater(
		noinline factory: () -> T,
	): Unit = simplyDIContainer.addDependencyLater<T>(
		scopeName = scopeName,
		factory = factory
	)

	/**
	 * Use it to replace a dependency.
	 * @param factory with your dependency.
	 **/
	public inline fun <reified T : Any> replaceDependencyNow(
		noinline factory: () -> T,
	): Unit = simplyDIContainer.replaceDependencyNow<T>(
		scopeName = scopeName,
		factory = factory
	)

	/**
	 * Use it to replace a dependency.
	 * @param factory with your dependency.
	 **/
	public inline fun <reified T : Any> replaceDependencyLater(
		noinline factory: () -> T,
	): Unit = simplyDIContainer.replaceDependencyLater<T>(
		scopeName = scopeName,
		factory = factory
	)

	/**
	 * Use it to get the dependency.
	 * @throws SimplyDINotFoundException if the dependency not created you receive.
	 **/
	@Throws(SimplyDINotFoundException::class)
	public inline fun <reified T> getDependency(): T = simplyDIContainer.getDependency<T>(
		scopeName = scopeName
	)

	/**
	 * Use it to get the dependency.
	 * @throws SimplyDINotFoundException if the dependency not created you receive.
	 **/
	@Throws(SimplyDINotFoundException::class)
	public inline fun <reified T : Any> getDependencyByLazy(
		clazz: KClass<T>,
	): SimplyDILazyWrapper<T> = simplyDIContainer.getDependencyByLazy<T>(
		scopeName = scopeName,
		clazz = clazz
	)

	/**
	 * Use it to get new dependency every call.
	 * @throws SimplyDINotFoundException if the dependency not created you receive.
	 **/
	@Throws(SimplyDINotFoundException::class)
	public inline fun <reified T> getFactoryDependency(): T = simplyDIContainer.getFactoryDependency<T>(
		scopeName = scopeName
	)

	/**
	 * Use it to delete the dependency from scope.
	 **/
	public inline fun <reified T : Any> deleteDependency(): Unit = simplyDIContainer.deleteDependency<T>(
		scopeName = scopeName
	)

	/**
	 * Use it to bind scopes.
	 * @param listOfScopes List of names of scopes.
	 **/
	@Suppress("DEPRECATION")
	public fun addChainScopes(listOfScopes: List<String>): Unit = simplyDIContainer.addChainScopes(
		listOfScopes = listOfScopes
	)

	/**
	 * Use it to delete bind of scopes.
	 * @param listOfScopes List of names of scopes.
	 **/
	@Suppress("DEPRECATION")
	public fun deleteChainedScopes(listOfScopes: List<String>): Unit = simplyDIContainer.deleteChainedScopes(
		listOfScopes = listOfScopes
	)
}

/**
 * Initialize method for new container.
 * @param scopeName name of the new container.
 * @param simplyLogLevel where you can set level of logs for you needs for example for release
 * would do use [SimplyLogLevel.EMPTY] for debug [SimplyLogLevel.FULL].
 * @param isSearchInScope if you want to use this container like data store or you need
 * to share dependencies from this container you would set value like true or you can bind them [SimplyDIContainerDSL.addChainScopes].
 * @param builder context of [SimplyDIContainerDSL] where you can interact with [SimplyDIContainer] more convenient.
 */
public fun initializeSimplyDIContainer(
	scopeName: String = DEFAULT_SCOPE_NAME,
	simplyLogLevel: SimplyLogLevel = SimplyLogLevel.EMPTY,
	isSearchInScope: Boolean = true,
	builder: SimplyDIContainerDSL.() -> Unit,
): SimplyDIContainer {
	val containerBuilder = SimplyDIContainerDSL()

	return containerBuilder.initialize(
		scopeName = scopeName,
		simplyLogLevel = simplyLogLevel,
		isSearchInScope = isSearchInScope,
		context = builder
	)
}
