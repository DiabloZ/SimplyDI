package su.vi.simplydiapp

import android.app.Application
import android.util.Log
import su.vi.simply.di.android.initializeSimplyDIAndroidContainer
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.core.utils.SimplyLogLevel
import su.vi.simply.di.core.utils.initializeSimplyDIContainer
import su.vi.simplydiapp.for_test.Bububu
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class App : Application() {
	@OptIn(ExperimentalTime::class)
	override fun onCreate() {
		super.onCreate()
		val time = measureTime {
			/*	SimplyDIContainer.instance.initializeAndroid(context = this, simplyLogLevel = SimplyLogLevel.FULL)
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
				SimplyDIContainer.instance.addDependencyLater<Bububu>(scopeName = "12345") { Bububu() */
			/**This will send message about replace to console.**//* }
			SimplyDIContainer.instance.replaceDependencyLater<Bububu>(scopeName = "12345") { Bububu() }
			SimplyDIContainer.instance.addChainScopes( listOfScopes = listOf("6", "12345", DEFAULT_SCOPE_NAME, "5"))
			addDependencyNow<Bububu>() { Bububu() }
			addDependencyNow<Bububu>(scopeName = "1") { Bububu() }
			addDependencyLater<Bububu>(scopeName = "12345") { Bububu() }
			addDependencyLater<Bububu>(scopeName = "12345") { Bububu() /**This will send message about replace to console.**/ }
			replaceDependencyLater<Bububu>(scopeName = "12345") { Bububu() }
			addChainScopes( listOfScopes = listOf("6", "12345", DEFAULT_SCOPE_NAME, "5"))
			*/
			initializeSimplyDIAndroidContainer(application = this, simplyLogLevel = SimplyLogLevel.FULL) {

			}
			initializeSimplyDIContainer(simplyLogLevel = SimplyLogLevel.FULL) {
				addDependencyNow { Bububu() }
			}
			initializeSimplyDIContainer(scopeName = "6", isSearchInScope = false) {}
			initializeSimplyDIContainer(
				scopeName = "6",
				isSearchInScope = false,
				simplyLogLevel = SimplyLogLevel.FULL
			) {}
			initializeSimplyDIContainer(scopeName = "5", isSearchInScope = true) {}
			initializeSimplyDIContainer(
				scopeName = "4",
				isSearchInScope = false,
				simplyLogLevel = SimplyLogLevel.FULL
			) {}
			initializeSimplyDIContainer(scopeName = "3", isSearchInScope = true) {}
			initializeSimplyDIContainer(scopeName = "2", isSearchInScope = true) {}
			initializeSimplyDIContainer(scopeName = "1", isSearchInScope = true) {
				addDependencyNow { Bububu() }
			}
			initializeSimplyDIContainer(
				scopeName = "12345",
				isSearchInScope = true,
				simplyLogLevel = SimplyLogLevel.FULL
			) {
				addDependencyLater { Bububu() }
				addDependencyLater { Bububu() }
/*				addDependencyLater { Bububu2(
					getFactoryDependency()
				) }*/
				replaceDependencyLater { Bububu() }
				addChainScopes(listOfScopes = listOf("6", "12345", DEFAULT_SCOPE_NAME, "5"))
			}

				//su.vi.simplydiapp.for_test.BububuGenerated().greet()

			/*			val test = initializeSimplyDIContainer(
							scopeName = "abc",
							simplyLogLevel = SimplyLogLevel.FULL,
							isSearchInScope = false,
						) {
							addDependencyNow { "Something" }
							addDependencyLater { "otherString" + getDependency<String>() }
						}
						Log.e(TAG, test.toString())*/
		}.inWholeMilliseconds

		Log.e(TAG, "INITED FOR - $time ms")
	}

	companion object {
		private const val TAG = "SIMPLY DI CONTAINER"
	}
}
