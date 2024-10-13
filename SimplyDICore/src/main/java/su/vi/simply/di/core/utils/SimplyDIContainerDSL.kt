package su.vi.simply.di.core.utils

import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME

public class SimplyDIContainerDSL {
	public lateinit var scopeName: String
	private var isSearchInScope: Boolean = true

	public val simplyDIContainer: SimplyDIContainer by lazy {
		SimplyDIContainer(
			scopeName = scopeName,
			isSearchInScope = isSearchInScope
		)
	}

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

	public inline fun <reified T : Any> addDependencyNow(
		noinline factory: () -> T,
	): Unit = simplyDIContainer.addDependencyNow<T>(
		scopeName = scopeName,
		factory = factory
	)

	public inline fun <reified T : Any> addDependencyLater(
		noinline factory: () -> T,
	): Unit = simplyDIContainer.addDependencyLater<T>(
		scopeName = scopeName,
		factory = factory
	)

	public inline fun <reified T : Any> replaceDependencyNow(
		noinline factory: () -> T,
	): Unit = simplyDIContainer.replaceDependencyNow<T>(
		scopeName = scopeName,
		factory = factory
	)

	public inline fun <reified T : Any> replaceDependencyLater(
		noinline factory: () -> T,
	): Unit = simplyDIContainer.replaceDependencyLater<T>(
		scopeName = scopeName,
		factory = factory
	)

	@Throws(SimplyDINotFoundException::class)
	public inline fun <reified T> getDependency(): T = simplyDIContainer.getDependency<T>(
		scopeName = scopeName
	)

	@Throws(SimplyDINotFoundException::class)
	public inline fun <reified T> getFactoryDependency(): T = simplyDIContainer.getFactoryDependency<T>(
		scopeName = scopeName
	)

	public inline fun <reified T : Any> deleteDependency(): Unit = simplyDIContainer.deleteDependency<T>(
		scopeName = scopeName
	)

	@Suppress("DEPRECATION")
	public fun addChainScopes(listOfScopes: List<String>): Unit = simplyDIContainer.addChainScopes(
		listOfScopes = listOfScopes
	)

	@Suppress("DEPRECATION")
	public fun deleteChainedScopes(listOfScopes: List<String>): Unit = simplyDIContainer.deleteChainedScopes(
		listOfScopes = listOfScopes
	)
}

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
