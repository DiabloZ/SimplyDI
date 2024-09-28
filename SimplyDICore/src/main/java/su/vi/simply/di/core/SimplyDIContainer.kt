package su.vi.simply.di.core

import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import kotlin.concurrent.thread
import kotlin.reflect.KClass
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * Singletone контейнер с [SimplyDIScope], нужен для первичной инициализации и получения зависимостей
 * по всему приложению.
 */
public class SimplyDIContainer private constructor() {

	private val mapContainers = mutableMapOf<String, SimplyDIScope>()

	/**
	 * Метод для первичной инициализации, является корнем зависомостей,
	 * должен находится максимально близко к app context.
	 */
	public fun initialize(
		scopeName: String = DEFAULT_SCOPE_NAME,
		isSearchInScope: Boolean = true,
	): Unit = synchronized(this) {
		if (mapContainers.containsKey(scopeName)) {
			SimplyDILogger.e(TAG, String.format(LOG_INIT_ALREADY, scopeName))
			return
		}
		mapContainers[scopeName] = SimplyDIScope(scopeName = scopeName, isSearchInScope = isSearchInScope)

		SimplyDILogger.d(TAG, String.format(LOG_INIT, scopeName))
	}

	@Throws(SimplyDINotFoundException::class)
	public fun <T : Any> addDependencyNow(
		scopeName: String = DEFAULT_SCOPE_NAME,
		clazz: KClass<*>,
		factory: () -> T,
	): Unit = synchronized(this) {
		mapContainers[scopeName]?.createDependencyNow(
			clazz = clazz,
			factory = factory
		) ?: kotlin.run {
			throw SimplyDINotFoundException(
				String.format(TRY_TO_CREATE_DEP_WHEN_SCOPE_IS_NOT_CREATED, clazz, scopeName)
			)
		}
		SimplyDILogger.d(TAG, "$CREATE_DEP_IMMEDIATELY${clazz}")
	}

	/**
	 * Метод для получения зависимости из [SimplyDIScope].
	 * @return T
	 * @throws SimplyDINotFoundException если зависимость не будет найдена
	 **/
	@Throws(SimplyDINotFoundException::class)
	public fun <T : Any> addDependencyLater(
		scopeName: String = DEFAULT_SCOPE_NAME,
		clazz: KClass<*>,
		factory: () -> T,
	): Unit = synchronized(this) {
		mapContainers[scopeName]?.createDependencyLater(
			clazz = clazz,
			factory = factory
		) ?: kotlin.run {
			throw SimplyDINotFoundException(
				String.format(TRY_TO_CREATE_DEP_WHEN_SCOPE_IS_NOT_CREATED, clazz, scopeName)
			)
		}
		SimplyDILogger.d(TAG, "$CREATE_DEP_LAZY${clazz}")
	}

	/**
	 * Метод для удаления зависимости из [SimplyDIScope] инициализированных и конструктор для инициализации.
	 **/
	public fun deleteDependency(
		clazz: KClass<*>,
		scopeName: String = DEFAULT_SCOPE_NAME,
	): Unit = synchronized(this) {
		mapContainers[scopeName]?.delete(clazz)
		SimplyDILogger.d(TAG, "$DELETE_DEP${clazz}")
	}

	/**
	 * Метод для получения зависимости из [SimplyDIScope].
	 * @return T
	 * @throws SimplyDINotFoundException если зависимость не будет найдена с добавлением в контейнер
	 **/
	@Throws(SimplyDINotFoundException::class)
	public fun <T : Any> getDependency(
		clazz: KClass<*>,
		scopeName: String = DEFAULT_SCOPE_NAME,
	): T {
		SimplyDILogger.d(TAG, "$GET_DEP_SINGLE${clazz}")
		return mapContainers[scopeName]?.getDependency(clazz)
			?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
	}

	/**
	 * Метод для получения зависимости из [SimplyDIScope].
	 * @return T
	 * @throws SimplyDINotFoundException если зависимость не будет найдена без добавления в контейнер
	 **/
	@Throws(SimplyDINotFoundException::class)
	public fun <T : Any> getFactoryDependency(
		clazz: KClass<*>,
		scopeName: String = DEFAULT_SCOPE_NAME,
	): T {
		SimplyDILogger.d(TAG, "$GET_DEP_FACTORY${clazz} ")
		return mapContainers[scopeName]?.getFactoryDependency(clazz = clazz)
			?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
	}

	@Suppress("UNCHECKED_CAST")
	@Throws(SimplyDINotFoundException::class)
	public fun <T: Any> getByClassAnyway(
		scopeName: String = DEFAULT_SCOPE_NAME,
		clazz: KClass<*>,
	): T {
		SimplyDILogger.d(TAG, "$GET_DEP_FACTORY_WITH_ERROR$clazz")
		return mapContainers[scopeName]?.getByClass(clazz)
			?: mapContainers
				.filter { entry -> entry.value.isSearchInScope }
				.mapNotNull { diScopeEntry -> diScopeEntry.value.getByClass(clazz) }
				.firstOrNull() as? T
			?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
	}

	@Suppress("UNCHECKED_CAST")
	@OptIn(ExperimentalTime::class)
	fun <T: Any> depBenchmark(
		clazz: KClass<*>,
	) {
		val mainTimes = 10
		val times = 100
		var mainusuTimer = 0L
		var mainseqTimer = 0L
		thread {
			repeat(mainTimes) {
				var usuTimer = 0L
				var seqTimer = 0L

				repeat(times) {
					seqTimer += measureTime {
						mapContainers.asSequence()
							.filter { entry -> entry.value.isSearchInScope }
							.mapNotNull { diScopeEntry -> diScopeEntry.value.getByClass(clazz) }
							.firstOrNull() as? T
					}.inWholeNanoseconds



					usuTimer += measureTime {
						mapContainers
							.filter { entry -> entry.value.isSearchInScope }
							.mapNotNull { diScopeEntry -> diScopeEntry.value.getByClass(clazz) }
							.firstOrNull() as? T
					}.inWholeNanoseconds
				}
				mainusuTimer += usuTimer
				mainseqTimer += seqTimer
				SimplyDILogger.e(TAG, "asSequence -  ${seqTimer / times}")
				SimplyDILogger.e(TAG, "usualArray -  ${usuTimer / times}")
			}
			SimplyDILogger.e(TAG, "Main asSequence -  ${mainseqTimer/(mainTimes *times)}")
			SimplyDILogger.e(TAG, "Main usualArray -  ${mainusuTimer/(mainTimes *times)}")
		}
	}



	@Throws(SimplyDINotFoundException::class)
	public fun <T: Any> getByClass(
		scopeName: String = DEFAULT_SCOPE_NAME,
		clazz: KClass<*>,
	): T? {
		SimplyDILogger.d(TAG, "$GET_DEP_FACTORY_NULLABLE$clazz")
		return mapContainers[scopeName]?.getByClass<T>(clazz)
	}

	public companion object {
		private const val TAG = "SIMPLY DI CONTAINER"

		private const val LOG_INIT = "Scope with name - %s has been initialized"
		private const val LOG_INIT_ALREADY = "Scope with name - %s has already been initialized"
		private const val SCOPE_IS_NOT_INITIALIZED = "∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇\n" +
			"|||||||||||||||| 1.Scope is not initialized.\n" +
			"|||||||||||||||| 2.It is with parameter \"isSearchInScope = false\".\n" +
			"|||||||||||||||| 3.There isn't any dependency.\n"+
			"|||||||||||||||| 4.You try get a dependency from other scope.\n"+
			"|||||||||||||||| !!!WARNING scope by default is \"$DEFAULT_SCOPE_NAME\"!!! ||||||||||||||||"
		private const val TRY_TO_CREATE_DEP_WHEN_SCOPE_IS_NOT_CREATED = "You try to create dependency !!!\"%s\"!!! in to scope with name !!!\"%s\"!!! but it's not created."
		private const val CREATE_DEP_IMMEDIATELY = "Добавлена зависимость немеденно - "
		private const val CREATE_DEP_LAZY = "Добавлена зависимость отложенно - "
		private const val DELETE_DEP = "Удалена зависимость - "
		private const val GET_DEP_SINGLE = "Запрошена зависимость с добавлением в контейнер - "
		private const val GET_DEP_FACTORY = "Запрошена зависимость без добавления - "
		private const val GET_DEP_FACTORY_WITH_ERROR = "Запрошена зависимость без добавления с ошибкой - "
		private const val GET_DEP_FACTORY_NULLABLE = "Запрошена зависимость без добавления нулабельно - "
		public val instance: SimplyDIContainer = SimplyDIContainer()
	}
}