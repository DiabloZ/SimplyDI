package su.vi.simplydiapp

import android.app.Application
import android.util.Log
import su.vi.simply.di.android.initializeSimplyDIAndroidContainer
import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.lazy.SimplyDILazyWrapper
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.core.utils.SimplyLogLevel
import su.vi.simply.di.core.utils.getDependency
import su.vi.simply.di.core.utils.initializeSimplyDIContainer
import su.vi.simplydiapp.for_test.AnyClass
import su.vi.simplydiapp.for_test.TestLazyClass
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
				SimplyDIContainer.instance.addDependencyNow<AnyClass>() { AnyClass() }
				SimplyDIContainer.instance.addDependencyNow<AnyClass>(scopeName = "1") { AnyClass() }
				SimplyDIContainer.instance.addDependencyLater<AnyClass>(scopeName = "12345") { AnyClass() }
				SimplyDIContainer.instance.addDependencyLater<AnyClass>(scopeName = "12345") { AnyClass() */
			/**This will send message about replace to console.**//* }
			SimplyDIContainer.instance.replaceDependencyLater<AnyClass>(scopeName = "12345") { AnyClass() }
			SimplyDIContainer.instance.addChainScopes( listOfScopes = listOf("6", "12345", DEFAULT_SCOPE_NAME, "5"))
			addDependencyNow<AnyClass>() { AnyClass() }
			addDependencyNow<AnyClass>(scopeName = "1") { AnyClass() }
			addDependencyLater<AnyClass>(scopeName = "12345") { AnyClass() }
			addDependencyLater<AnyClass>(scopeName = "12345") { AnyClass() /**This will send message about replace to console.**/ }
			replaceDependencyLater<AnyClass>(scopeName = "12345") { AnyClass() }
			addChainScopes( listOfScopes = listOf("6", "12345", DEFAULT_SCOPE_NAME, "5"))
			*/
			initializeSimplyDIAndroidContainer(application = this, simplyLogLevel = SimplyLogLevel.FULL) {

			}
			initializeSimplyDIContainer(simplyLogLevel = SimplyLogLevel.FULL) {
				addDependencyNow { AnyClass() }
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
				addDependencyNow { AnyClass() }
			}
			initializeSimplyDIContainer(
				scopeName = "12345",
				isSearchInScope = true,
				simplyLogLevel = SimplyLogLevel.FULL
			) {
				measureTime {
					addDependencyLater { AnyClass() }
				}.let {
					println(
						"LAZY(NOT) ADD DEPENDENCY - $it"
					)
				}

				addDependencyLater { TestLazyClass(anyClass = getDependency()) }
				measureTime {
					addDependencyLater { SimplyDILazyWrapper<TestLazyClass>(lazyValue = { getDependency() }) }
				}.let {
					println(
						"LAZY ADD DEPENDENCY - $it"
					)
				}

				replaceDependencyLater { AnyClass() }
				addChainScopes(listOfScopes = listOf("6", "12345", DEFAULT_SCOPE_NAME, "5"))
			}

			/*			val test = initializeSimplyDIContainer(
							scopeName = "abc",
							simplyLogLevel = SimplyLogLevel.FULL,
							isSearchInScope = false,
						) {
							addDependencyNow { "Something" }
							addDependencyLater { "otherString" + getDependency<String>() }
						}
						Log.e(TAG, test.toString())*/
		}

		measureTime {
			SimplyDIContainer.instance.getDependency<AnyClass>("12345")
		}.let {
			println(
				"LAZY(NOT1) GET DEPENDENCY- $it"
			)
		}

		measureTime {
			SimplyDIContainer.instance.getDependency<TestLazyClass>("12345")
		}.let {
			println(
				"LAZY(NOT2) GET DEPENDENCY- $it"
			)
		}

		measureTime {
			SimplyDIContainer.instance.getDependency<SimplyDILazyWrapper<TestLazyClass>>("12345")
		}.let {
			println(
				"LAZY GET DEPENDENCY- $it"
			)
		}

		val test: SimplyDILazyWrapper<TestLazyClass> = SimplyDIContainer.instance.getDependency("12345")

		measureTime {
			test.value
		}.let {
			println(
				"LAZY GET VALUE- $it"
			)
		}
		measureTime {
			test.value.anyClass
		}.let {
			println(
				"LAZY GET VALUE ANY CLASS- $it"
			)
		}

		Log.e(TAG, "INITED FOR - $time")
	}

	companion object {
		private const val TAG = "SIMPLY DI CONTAINER"
	}
}
