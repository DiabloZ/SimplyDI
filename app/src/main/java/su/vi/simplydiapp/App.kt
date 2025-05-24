package su.vi.simplydiapp

import android.app.Application
import android.util.Log
import su.vi.kdi.BitSetDIContainer
import su.vi.kdi.Inject
import su.vi.kdi.Named
import su.vi.simply.di.android.initializeAndroid
import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.entry_point.addDependencyLater
import su.vi.simply.di.core.lazy.SimplyDILazyWrapper
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.core.utils.SimplyLogLevel
import su.vi.simply.di.core.entry_point.getDependency
import su.vi.simply.di.core.entry_point.getDependencyByLazy
import su.vi.simply.di.core.entry_point.initialize
import su.vi.simply.di.core.entry_point.initializeContainer
import su.vi.simplydiapp.App.Companion.TAG
import su.vi.simplydiapp.App.Companion.container
import su.vi.simplydiapp.for_test.AnyClass
import su.vi.simplydiapp.for_test.TestLazyClass
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

public class App : Application() {
	@OptIn(ExperimentalTime::class)
	override fun onCreate() {
		super.onCreate()

		kotlin.repeat(10) {
			tryToUseSimpleDIContainer(this)
		}
		kotlin.repeat(10) {
			toTryUseKDI(this)
		}
	}

	companion object {
		internal const val TAG = "SIMPLY DI CONTAINER"
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

	/*	implementation(libs.simply.di.core)
		implementation(libs.simply.di.android)
		implementation(libs.simply.di.viewmodel)
		implementation(libs.simply.di.compose)*/
	fun init() {
		container = SimplyDIContainer.initialize(CONTAINER_NAME2) {
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

private fun toTryUseKDI(app: Application) {
	val time = measureTime {
		var container2 = BitSetDIContainer()
		container2.registerAuto(ConsoleLogger::class.java)
		container2.registerAuto(AuditService::class.java)
		container2.registerAuto(AuditService2::class.java)
		container2.register { app }
		container2.register(Logger::class.java, name = "console") {
			ConsoleLogger()
		}

		container2.register(Logger::class.java, name = "file") {
			FileLogger()
		}
		val audit2 = container2.resolve(AuditService2::class.java)
		val audit = container2.resolve(AuditService::class.java)
		audit.logger.log("Audit started.")
		audit2.logger.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!")
// Автоматическая регистрация
		container2.registerAuto<Impl1>()
		container2.registerAuto<Impl2>()
		container2.registerAuto<Impl3>()
		container2.register<E> {
			Impl4()
		}

		// Регистрируем точку входа
		container2.registerAuto<AppRunner>()

		// Запуск
		val app1 = container2.resolve<AppRunner>()
		val app2 = container2.resolve<AppRunner>()
		val app3 = container2.resolve<AppRunner>()
		val app4 = container2.resolve<AppRunner>()
		val app5 = container2.resolve<AppRunner>()
		val app6 = container2.resolve<AppRunner>()
		app1.run()
	}
	Log.e("KDI CONTAINER", "INITED FOR - $time")
}

private fun tryToUseSimpleDIContainer(app: Application) {
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
		SimplyDIContainer.initializeAndroid(application = app, simplyLogLevel = SimplyLogLevel.FULL) {

		}
		SimplyDIContainer.initialize(simplyLogLevel = SimplyLogLevel.FULL) {
			addDependencyNow { AnyClass() }
		}
		SimplyDIContainer.initialize(scopeName = "6", isSearchInScope = false) {}
		SimplyDIContainer.initialize(
			scopeName = "6",
			isSearchInScope = false,
			simplyLogLevel = SimplyLogLevel.FULL
		) {}
		SimplyDIContainer.initialize(scopeName = "5", isSearchInScope = true) {}
		SimplyDIContainer.initialize(
			scopeName = "4",
			isSearchInScope = false,
			simplyLogLevel = SimplyLogLevel.FULL
		) {}
		SimplyDIContainer.initialize(scopeName = "3", isSearchInScope = true) {}
		SimplyDIContainer.initialize(scopeName = "2", isSearchInScope = true) {}
		SimplyDIContainer.initialize(scopeName = "1", isSearchInScope = true) {
			addDependencyNow { AnyClass() }
		}
		SimplyDIContainer.initialize(
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
	val someContainer = SimplyDIContainer("someContainer", false)
	someContainer.initializeContainer()
	someContainer.addDependencyLater(clazz = AnyClass::class) { AnyClass() }
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

	Log.e(App.TAG, "INITED FOR - $time")
	container = SimplyDIContainer.initialize(CONTAINER_NAME1) {
		addChainScopes(listOfScopes = listOf(CONTAINER_NAME1, CONTAINER_NAME2))
	}
}

interface Logger {
	fun log(msg: String)
}

class ConsoleLogger : Logger {
	override fun log(msg: String) {
		println("[Console] $msg")
	}
}

class FileLogger : Logger {
	override fun log(msg: String) {
		println("[File] $msg")
	}
}

class AuditService @Inject constructor(
	@Named("file") val logger: Logger,
)

class AuditService2(
	val logger: ConsoleLogger,
)

interface A {
	fun doA(): String
}

interface B {
	fun doB(): String
}

interface C {
	fun doC(): String
}

interface D {
	fun doD(): String
}

interface E {
	fun doE(): String
}

class Impl1 @Inject constructor() : A, B {
	override fun doA() = "A from Impl1"
	override fun doB() = "B from Impl1"
}

class Impl2 @Inject constructor(private val dep: A) : B, C {
	override fun doB() = "B from Impl2 with ${dep.doA()}"
	override fun doC() = "C from Impl2"
}

class Impl3 @Inject constructor(private val b: B, private val c: C) : D {
	override fun doD() = "D from Impl3 with ${b.doB()} and ${c.doC()}"
}

class Impl4 : E {
	override fun doE() = "E from Impl4"
}

class AppRunner(
	private val a: A,
	private val b: B,
	private val c: C,
	private val d: D,
	private val e: E,
) {

	@Inject
	constructor(
		a: A,
		b: B,
		c: C,
		d: D,
		e: E,
		@Named("console") logger: Logger,
	) : this(a, b, c, d, e) {
		logger.log("AppRunner initialized with dependencies: $a, $b, $c, $d, $e")
	}

	fun run() {
		println(a.doA())
		println(b.doB())
		println(c.doC())
		println(d.doD())
		println(e.doE())
	}
}
