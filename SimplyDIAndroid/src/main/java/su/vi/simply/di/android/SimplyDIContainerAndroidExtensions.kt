package su.vi.simply.di.android

import android.content.Context
import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.core.utils.SimplyLogLevel
import su.vi.simply.di.core.utils.addDependencyNow
import su.vi.simply.di.core.utils.initialize

/*
class SimplyDIContainerAndroidExtensions {
}*/

public fun SimplyDIContainer.initializeAndroid(
	context: Context,
	simplyLogLevel: SimplyLogLevel = SimplyLogLevel.EMPTY,
	scopeName: String = DEFAULT_SCOPE_NAME,
){
	initialize(
		scopeName = scopeName,
		simplyLogLevel = simplyLogLevel
	)
	addDependencyNow(scopeName = scopeName) { context }
	addDependencyNow(scopeName = scopeName) { context.resources }
}