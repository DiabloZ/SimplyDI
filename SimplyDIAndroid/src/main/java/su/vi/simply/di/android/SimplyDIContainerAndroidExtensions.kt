package su.vi.simply.di.android

import android.app.Application
import android.content.Context
import android.content.res.Resources
import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.core.utils.SimplyDIContainerDSL
import su.vi.simply.di.core.utils.SimplyLogLevel
import su.vi.simply.di.core.utils.initializeSimplyDIContainer

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
