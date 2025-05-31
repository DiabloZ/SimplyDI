package su.vi.simplydiapp

import android.app.Application
import android.util.Log
import su.vi.kdi.KDIContainerTemp
import su.vi.kdi.Inject
import su.vi.kdi.Named
import su.vi.kdi.core.KDIContainer
import su.vi.kdi.core.entry_point.addDependency
import su.vi.kdi.core.entry_point.addDependencyAuto
import su.vi.kdi.core.entry_point.getDependency
import su.vi.kdi.core.entry_point.initialize
import su.vi.kdi.core.entry_point.initializeContainer
import su.vi.kdi.core.utils.KDILogLevel
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
		println("INITED FOR")
		kotlin.repeat(10) {
			toTryUseKDI2(this)
		}
		println("INITED FOR")
		kotlin.repeat(10) {
			toTryUseKDI(this)
		}

		println("INITED FOR")
		kotlin.repeat(10) {
			toTryUseKDIDSL(this)
		}
		println("INITED FOR")
		kotlin.repeat(10) {
			toTryUseKDIDSL2(this)
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
private val CONTAINER_NAME3 = "testSomeContainer3"

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

private fun toTryUseKDIDSL(app: Application) {
	val time = measureTime {
		val kdiContainer = KDIContainer.initialize(
			scopeName = CONTAINER_NAME1,
			isSearchInScope = true,
		) {
			addDependency { app }
			addDependencyAuto<ConsoleLogger>()
			addDependencyAuto<AuditService>()
			addDependencyAuto<AuditService2>()
			addDependency<Logger>(name = "console") { ConsoleLogger() }
			addDependency<Logger>(name = "file") { FileLogger() }
			addDependencyAuto<Impl1>()
			addDependencyAuto<Impl2>()
			addDependencyAuto<Impl3>()
			addDependency<E>() { Impl4() }
			addDependencyAuto<AppRunner>()
		}
		val audit2 = kdiContainer.getDependency<AuditService2>()
		val audit = kdiContainer.getDependency<AuditService>()
		audit.logger.log("Audit started.")
		audit2.logger.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!")
		val app1 = kdiContainer.getDependency<AppRunner>()
		val app2 = kdiContainer.getDependency<AppRunner>()
		val app3 = kdiContainer.getDependency<AppRunner>()
		val app4 = kdiContainer.getDependency<AppRunner>()
		val app5 = kdiContainer.getDependency<AppRunner>()
		val app6 = kdiContainer.getDependency<AppRunner>()
		app1.run()
	}
	Log.e("KDI CONTAINER DSL", "INITED FOR - $time")
}

private fun toTryUseKDIDSL2(app: Application) {
	val time = measureTime {
		val kdiContainer = KDIContainer.initialize(
			scopeName = CONTAINER_NAME3,
			isSearchInScope = true,
		) {
			addDependency { app }
			addDependency<ConsoleLogger>() { ConsoleLogger() }
			addDependency<AuditService>() {
				AuditService(
					logger = getDependency(name = "file")
				)
			}
			addDependency<AuditService2>() {
				AuditService2(
					logger = getDependency(name = "console")
				)
			}
			addDependency<Logger>(name = "console") { ConsoleLogger() }
			addDependency<Logger>(name = "file") { FileLogger() }
			addDependency<A> {
				Impl1()
			}
			addDependency<Impl2>() {
				Impl2(
					dep = getDependency<A>()
				)
			}
			addDependency<D>() {
				Impl3(
					b = getDependency<B>(),
					c = getDependency<C>()
				)
			}
			addDependency<B>() { getDependency<Impl2>()}
			addDependency<C>() { getDependency<Impl2>() }
			addDependency<E>() { Impl4() }
			addDependency<AppRunner>() {
				AppRunner(
					a = getDependency<A>(),
					b = getDependency<B>(),
					c = getDependency<C>(),
					d = getDependency<D>(),
					e = getDependency<E>(),
					logger = getDependency(name = "console")
				)
			}
		}
		val audit2 = kdiContainer.getDependency<AuditService2>()
		val audit = kdiContainer.getDependency<AuditService>()
		audit.logger.log("Audit started.")
		audit2.logger.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!")
		val app1 = kdiContainer.getDependency<AppRunner>()
		val app2 = kdiContainer.getDependency<AppRunner>()
		val app3 = kdiContainer.getDependency<AppRunner>()
		val app4 = kdiContainer.getDependency<AppRunner>()
		val app5 = kdiContainer.getDependency<AppRunner>()
		val app6 = kdiContainer.getDependency<AppRunner>()
		app1.run()
	}
	Log.e("KDI CONTAINER DSL2", "INITED FOR - $time")
}

private fun toTryUseKDI(app: Application) {
	val time = measureTime {
		val kdiContainer2 = KDIContainer(
			scopeName = CONTAINER_NAME2,
			isSearchInScope = true,
		)
		kdiContainer2.initializeContainer(
			kdiLogLevel = KDILogLevel.Full(
				logger = object : su.vi.kdi.core.KDILogger {
					override fun d(tag: String, text: String, throwable: Throwable?) {
						Log.d(tag, text, throwable)
					}

					override fun e(tag: String, text: String, throwable: Throwable?) {
						Log.e(tag, text, throwable)
					}

					override fun wtf(tag: String, text: String, throwable: Throwable?) {
						Log.wtf(tag, text, throwable)
					}
				}
			),
		)

		kdiContainer2.addDependency { app }
		kdiContainer2.addDependencyAuto<ConsoleLogger>()
		kdiContainer2.addDependencyAuto<AuditService>()
		kdiContainer2.addDependencyAuto<AuditService2>()
		kdiContainer2.addDependency<Logger>(name = "console") { ConsoleLogger() }
		kdiContainer2.addDependency<Logger>(name = "file") { FileLogger() }
		kdiContainer2.addDependencyAuto<Impl1>()
		kdiContainer2.addDependencyAuto<Impl2>()
		kdiContainer2.addDependencyAuto<Impl3>()
		kdiContainer2.addDependency<E>() { Impl4() }
		kdiContainer2.addDependencyAuto<AppRunner>()

		val audit2 = kdiContainer2.getDependency<AuditService2>()
		val audit = kdiContainer2.getDependency<AuditService>()
		audit.logger.log("Audit started.")
		audit2.logger.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!")
		val app1 = kdiContainer2.getDependency<AppRunner>()
		val app2 = kdiContainer2.getDependency<AppRunner>()
		val app3 = kdiContainer2.getDependency<AppRunner>()
		val app4 = kdiContainer2.getDependency<AppRunner>()
		val app5 = kdiContainer2.getDependency<AppRunner>()
		val app6 = kdiContainer2.getDependency<AppRunner>()
		app1.run()
	}
	Log.e("KDI CONTAINER", "INITED FOR - $time")
/*	val time = measureTime {
		var container2 = KDIContainerTemp()
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
	Log.e("KDI CONTAINER", "INITED FOR - $time")*/
}

private fun toTryUseKDI2(app: Application) {
	val time = measureTime {
		val kdiContainer2 = KDIContainer(
			scopeName = CONTAINER_NAME2,
			isSearchInScope = true,
		)
		kdiContainer2.initializeContainer(
			kdiLogLevel = KDILogLevel.Full(
				logger = object : su.vi.kdi.core.KDILogger {
					override fun d(tag: String, text: String, throwable: Throwable?) {
						Log.d(tag, text, throwable)
					}

					override fun e(tag: String, text: String, throwable: Throwable?) {
						Log.e(tag, text, throwable)
					}

					override fun wtf(tag: String, text: String, throwable: Throwable?) {
						Log.wtf(tag, text, throwable)
					}
				}
			),
		)
		kdiContainer2.addDependency { app }
		kdiContainer2.addDependency<ConsoleLogger>() { ConsoleLogger() }
		kdiContainer2.addDependency<AuditService>() {
			AuditService(
				logger = kdiContainer2.getDependency(name = "file")
			)
		}
		kdiContainer2.addDependency<AuditService2>() {
			AuditService2(
				logger = kdiContainer2.getDependency(name = "console")
			)
		}
		kdiContainer2.addDependency<Logger>(name = "console") { ConsoleLogger() }
		kdiContainer2.addDependency<Logger>(name = "file") { FileLogger() }
		kdiContainer2.addDependency<A> {
			Impl1()
		}
		kdiContainer2.addDependency<Impl2>() {
			Impl2(
				dep = kdiContainer2.getDependency<A>()
			)
		}
		kdiContainer2.addDependency<D>() {
			Impl3(
				b = kdiContainer2.getDependency<B>(),
				c = kdiContainer2.getDependency<C>()
			)
		}
		kdiContainer2.addDependency<B>() { kdiContainer2.getDependency<Impl2>()}
		kdiContainer2.addDependency<C>() { kdiContainer2.getDependency<Impl2>() }
		kdiContainer2.addDependency<E>() { Impl4() }
		kdiContainer2.addDependency<AppRunner>() {
			AppRunner(
				a = kdiContainer2.getDependency<A>(),
				b = kdiContainer2.getDependency<B>(),
				c = kdiContainer2.getDependency<C>(),
				d = kdiContainer2.getDependency<D>(),
				e = kdiContainer2.getDependency<E>(),
				logger = kdiContainer2.getDependency(name = "console")
			)
		}

		val audit2 = kdiContainer2.getDependency<AuditService2>()
		val audit = kdiContainer2.getDependency<AuditService>()
		audit.logger.log("Audit started.")
		audit2.logger.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!")
		val app1 = kdiContainer2.getDependency<AppRunner>()
		val app2 = kdiContainer2.getDependency<AppRunner>()
		val app3 = kdiContainer2.getDependency<AppRunner>()
		val app4 = kdiContainer2.getDependency<AppRunner>()
		val app5 = kdiContainer2.getDependency<AppRunner>()
		val app6 = kdiContainer2.getDependency<AppRunner>()
		app1.run()
	}
	Log.e("KDI CONTAINER", "INITED FOR - $time")
/*	val time = measureTime {
		var container2 = KDIContainerTemp()
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
	Log.e("KDI CONTAINER", "INITED FOR - $time")*/
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
