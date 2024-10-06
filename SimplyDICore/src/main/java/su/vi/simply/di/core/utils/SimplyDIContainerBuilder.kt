package su.vi.simply.di.core.utils

import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME

public class SimplyDIContainerBuilder {
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
		builder: SimplyDIContainerBuilder.() -> Unit
	): SimplyDIContainer {
		this.scopeName = scopeName
		simplyDIContainer.initialize(
			scopeName = scopeName,
			simplyLogLevel = simplyLogLevel,
			isSearchInScope = isSearchInScope
		)
		builder.invoke(this)
		return simplyDIContainer
	}


	public inline fun <reified T : Any> addDependencyNow(
		noinline factory: () -> T,
	) {
		simplyDIContainer.addDependencyNow(
			scopeName = scopeName,
			clazz = T::class,
			factory = factory
		)
	}
}

public fun initializeSimplyDIContainer(
	scopeName: String = DEFAULT_SCOPE_NAME,
	simplyLogLevel: SimplyLogLevel = SimplyLogLevel.EMPTY,
	isSearchInScope: Boolean = true,
	builder: SimplyDIContainerBuilder.() -> Unit
): SimplyDIContainer {
	val containerBuilder = SimplyDIContainerBuilder()

	return containerBuilder.initialize(
		scopeName = scopeName,
		simplyLogLevel = simplyLogLevel,
		isSearchInScope = isSearchInScope,
		builder = builder
	)
}

/*
public fun main (){
	val test = initializeSimplyDIContainer(
		scopeName = "abc",
		simplyLogLevel = SimplyLogLevel.EMPTY,
		isSearchInScope = false,
	) {
		addDependencyNow { "Something" }
	}
	test
}*/
