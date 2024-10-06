package su.vi.simplydiapp

import android.app.Application
import android.util.Log
import su.vi.simply.di.android.initializeAndroid
import su.vi.simply.di.core.utils.SimplyLogLevel
import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.core.utils.addChainScopes
import su.vi.simply.di.core.utils.addDependencyLater
import su.vi.simply.di.core.utils.addDependencyNow
import su.vi.simply.di.core.utils.initialize
import su.vi.simply.di.core.utils.initializeSimplyDIContainer
import su.vi.simply.di.core.utils.replaceDependencyLater
import su.vi.simplydiapp.for_test.Bububu
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class App: Application() {
	@OptIn(ExperimentalTime::class)
	override fun onCreate() {
		super.onCreate()
		val time = measureTime {
			SimplyDIContainer.instance.initializeAndroid(context = this, simplyLogLevel = SimplyLogLevel.FULL)
			SimplyDIContainer.instance.initialize(simplyLogLevel = SimplyLogLevel.FULL)
			SimplyDIContainer.instance.initialize(scopeName = "6", isSearchInScope = false)
			SimplyDIContainer.instance.initialize(scopeName = "6", isSearchInScope = false, simplyLogLevel = SimplyLogLevel.FULL)
			SimplyDIContainer.instance.initialize(scopeName = "5", isSearchInScope = true)
			SimplyDIContainer.instance.initialize(scopeName = "4", isSearchInScope = false, simplyLogLevel = SimplyLogLevel.FULL)
			SimplyDIContainer.instance.initialize(scopeName = "3", isSearchInScope = true)
			SimplyDIContainer.instance.initialize(scopeName = "2", isSearchInScope = true)
			SimplyDIContainer.instance.initialize(scopeName = "1", isSearchInScope = true)
			SimplyDIContainer.instance.initialize(scopeName = "12345", isSearchInScope = true, simplyLogLevel = SimplyLogLevel.FULL)
			SimplyDIContainer.instance.addDependencyNow<Bububu>() { Bububu() }
			SimplyDIContainer.instance.addDependencyNow<Bububu>(scopeName = "1") { Bububu() }
			SimplyDIContainer.instance.addDependencyLater<Bububu>(scopeName = "12345") { Bububu() }
			SimplyDIContainer.instance.addDependencyLater<Bububu>(scopeName = "12345") { Bububu() /**This will send message about replace to console.**/ }
			SimplyDIContainer.instance.replaceDependencyLater<Bububu>(scopeName = "12345") { Bububu() }
			SimplyDIContainer.instance.addChainScopes( listOfScopes = listOf("6", "12345", DEFAULT_SCOPE_NAME, "5"))
			//SimplyDIContainer.instance.depBenchmark<Bububu>()
			/*		SimplyDIContainer.instance.deleteChainedScopes(
						listOfScopes = listOf("6", "12345", DEFAULT_SCOPE_NAME, "5")
					)*/
			val test = initializeSimplyDIContainer(
				scopeName = "abc",
				simplyLogLevel = SimplyLogLevel.FULL,
				isSearchInScope = false,
			) {
				addDependencyNow { "Something" }
			}
			Log.e(TAG, test.toString())
		}.inWholeMilliseconds

		Log.e(TAG, "INITED FOR - $time ms")
	}

	companion object {
		private const val TAG = "SIMPLY DI CONTAINER"
	}
}
