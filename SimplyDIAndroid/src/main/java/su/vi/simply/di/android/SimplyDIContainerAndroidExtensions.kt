package su.vi.simply.di.android

import android.app.Application
import android.content.Context
import android.content.res.Resources
import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.core.entry_point.SimplyDIContainerDSL
import su.vi.simply.di.core.utils.SimplyLogLevel
import su.vi.simply.di.core.entry_point.initializeSimplyDIContainer

/**
 * Initialize method for new container.
 * @param scopeName name of the new container.
 * @param simplyLogLevel where you can set level of logs for you needs for example for release
 * would do use [SimplyLogLevel.EMPTY] for debug [SimplyLogLevel.FULL].
 * @param isSearchInScope if you want to use this container like data store or you need
 * to share dependencies from this container you would set value like true or you can bind them [SimplyDIContainerDSL.addChainScopes].
 * @param builder context of [SimplyDIContainerDSL] where you can interact with [SimplyDIContainer] more convenient.
 */
public fun initializeSimplyDIAndroidContainer(
	application: Application,
	scopeName: String = DEFAULT_SCOPE_NAME,
	simplyLogLevel: SimplyLogLevel = SimplyLogLevel.EMPTY,
	isSearchInScope: Boolean = true,
	builder: SimplyDIContainerDSL.() -> Unit,
): SimplyDIContainer = initializeSimplyDIContainer(
	scopeName = scopeName,
	simplyLogLevel = simplyLogLevel,
	isSearchInScope = isSearchInScope
) {
	addDependencyNow<Context> { application }
	addDependencyNow<Application> { application }
	addDependencyNow<Resources> { application.resources }
	builder.invoke(this)
}
