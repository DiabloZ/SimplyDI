package su.vi.simplydiapp

import android.app.Application
import android.util.Log
import su.vi.simply.di.android.initializeSimplyDIAndroidContainer
import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.lazy.SimplyDILazyWrapper
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.core.utils.SimplyLogLevel
import su.vi.simply.di.core.entry_point.getDependency
import su.vi.simply.di.core.entry_point.getDependencyByLazy
import su.vi.simply.di.core.entry_point.initializeSimplyDIContainer
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
				//measureTime {
				//	addDependencyLater { SimplyDILazyWrapper<AnyClass>(clazz = AnyClass::class) { AnyClass() } }
				//}.let {
				//	println(
				//		"LAZY(NOT) ADD DEPENDENCY - $it"
				//	)
				//}

				addDependencyLater<TestLazyClass> {
					TestLazyClass(
						anyClass = getDependency<AnyClass>(),
						anyClassInWrapper = getDependencyByLazy<AnyClass>()
					)
				}
				//measureTime {
				//	addDependencyLater {
				//		SimplyDILazyWrapper<TestLazyClass>(
				//			clazz = TestLazyClass::class,
				//			lazyValue = { getDependency() }
				//		)
				//	}
				//}.let {
				//	println(
				//		"LAZY ADD DEPENDENCY - $it"
				//	)
				//}

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
			SimplyDIContainer.instance.getDependency<AnyClass>("12345").let {
				println(
					it
				)
			}
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
			SimplyDIContainer.instance.getDependency<TestLazyClass>(scopeName = "12345", clazz = TestLazyClass::class)
		}.let {
			println(
				"LAZY GET 1 DEPENDENCY- $it"
			)
		}

		measureTime {
			SimplyDIContainer.instance.getDependencyByLazy<TestLazyClass>(
				scopeName = "12345",
				clazz = TestLazyClass::class
			)
		}.let {
			println(
				"LAZY GET 2 DEPENDENCY- $it"
			)
		}

		val test: SimplyDILazyWrapper<TestLazyClass> =
			SimplyDIContainer.instance.getDependencyByLazy(scopeName = "12345", clazz = TestLazyClass::class)

		measureTime {
			test.value
		}.let {
			println(
				"LAZY GET VALUE- $it"
			)
		}

		measureTime {
			test.value.anyClass.let {
				println(
					it
				)
			}
		}.let {
			println(
				"LAZY GET VALUE ANY CLASS- $it"
			)
		}

		measureTime {
			test.value.anyClassInWrapper.value.let {
				println(
					it
				)
			}
		}.let {
			println(
				"LAZY GET VALUE ANY CLASS in wrapper- $it"
			)
		}

		Log.e(TAG, "INITED FOR - $time")
		container = initializeSimplyDIContainer(CONTAINER_NAME1) {
			addChainScopes(listOfScopes = listOf(CONTAINER_NAME1, CONTAINER_NAME2))
		}
	}

	companion object {
		private const val TAG = "SIMPLY DI CONTAINER"
		internal lateinit var container: SimplyDIContainer
		internal lateinit var container2: SimplyDIContainer
	}
}

private val CONTAINER_NAME1 = "testSomeContainer1"
private val CONTAINER_NAME2 = "testSomeContainer2"

public interface SomeStorage {
	fun getSecretString(): String
	fun setOtherString(other: String)
	fun getOtherString(): String
}

internal class SomeStorageImpl(
	private val secretString: String,
) : SomeStorage {

	override fun getSecretString(): String = secretString

	private var _otherString = ""
	override fun setOtherString(other: String) {
		_otherString = other
	}

	override fun getOtherString(): String {
		println("THIS !!!!!!!!!!!!!! - $this")
		return _otherString
	}
}

object SomePublicClassFromOtherModule {
	lateinit var container: SimplyDIContainer

	fun init() {
		container = initializeSimplyDIContainer(CONTAINER_NAME2) {
			addDependencyLater<SomeStorage> {
				SomeStorageImpl("secret_shhhhhh...")
			}
			addChainScopes(listOfScopes = listOf(CONTAINER_NAME1, CONTAINER_NAME2))
		}
	}

	fun getOther(): String {
		val so = container.getDependency<SomeStorage>().getOtherString()
		return so
	}
}
