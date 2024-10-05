package su.vi.simply.di.core

import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.core.utils.SimplyLogLevel
import su.vi.simply.di.core.utils.toSimplyDILogger
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
	private val chainedScopes = mutableMapOf<String, MutableList<List<String>>>()
	private var logger: SimplyDILogger = SimplyDILoggerEmpty()

	/**
	 * Метод для первичной инициализации, является корнем зависомостей,
	 * должен находится максимально близко к app context.
	 */
	internal fun initialize(
		scopeName: String = DEFAULT_SCOPE_NAME,
		simplyLogLevel: SimplyLogLevel = SimplyLogLevel.EMPTY,
		isSearchInScope: Boolean = true,
	): Unit = synchronized(this) {
		logger = simplyLogLevel.toSimplyDILogger()
		if (mapContainers.containsKey(scopeName)) {
			logger.e(TAG, String.format(LOG_INIT_ALREADY, scopeName))
			return
		}
		mapContainers[scopeName] = SimplyDIScope(scopeName = scopeName, isSearchInScope = isSearchInScope)

		logger.d(TAG, String.format(LOG_INIT, scopeName))
	}

	@Throws(SimplyDINotFoundException::class)
	public fun <T : Any> addDependencyNow(
		scopeName: String = DEFAULT_SCOPE_NAME,
		clazz: KClass<*>,
		factory: () -> T,
	): Unit = synchronized(this) {
		if (isDependencyInScope(scopeName = scopeName, clazz = clazz)){
			logger.e(TAG, String.format(REPLACE_ERR, clazz, scopeName))
			return@synchronized
		}
		mapContainers[scopeName]?.createDependencyNow(
			clazz = clazz,
			factory = factory
		) ?: kotlin.run {
			throw SimplyDINotFoundException(
				String.format(TRY_TO_CREATE_DEP_WHEN_SCOPE_IS_NOT_CREATED, clazz, scopeName)
			)
		}
		logger.d(TAG, "$CREATE_DEP_IMMEDIATELY${clazz}")
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
		if (isDependencyInScope(scopeName = scopeName, clazz = clazz)){
			logger.e(TAG, String.format(REPLACE_ERR, clazz, scopeName))
			return@synchronized
		}
		mapContainers[scopeName]?.createDependencyLater(
			clazz = clazz,
			factory = factory
		) ?: kotlin.run {
			throw SimplyDINotFoundException(
				String.format(TRY_TO_CREATE_DEP_WHEN_SCOPE_IS_NOT_CREATED, clazz, scopeName)
			)
		}
		logger.d(TAG, "$CREATE_DEP_LAZY${clazz}")
	}

	public fun <T : Any> replaceDependencyNow(
		scopeName: String = DEFAULT_SCOPE_NAME,
		clazz: KClass<*>,
		factory: () -> T,
	) {
		deleteDependency(clazz = clazz, scopeName = scopeName)
		addDependencyNow(scopeName = scopeName, clazz = clazz, factory = factory)
	}

	public fun <T : Any> replaceDependencyLater(
		scopeName: String = DEFAULT_SCOPE_NAME,
		clazz: KClass<*>,
		factory: () -> T,
	) {
		deleteDependency(clazz = clazz, scopeName = scopeName)
		addDependencyLater(scopeName = scopeName, clazz = clazz, factory = factory)
	}

	/**
	 * Метод для удаления зависимости из [SimplyDIScope] инициализированных и конструктор для инициализации.
	 **/
	public fun deleteDependency(
		clazz: KClass<*>,
		scopeName: String = DEFAULT_SCOPE_NAME,
	): Unit = synchronized(this) {
		mapContainers[scopeName]?.apply {
			delete(clazz)
			logger.d(TAG, "$DELETE_DEP${clazz}")
			return@synchronized
		}
		logger.d(TAG, "$DELETE_DEP_ERR${clazz}")
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
		logger.d(TAG, "$GET_DEP_SINGLE${clazz}")
		val scope = mapContainers[scopeName] ?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
		return scope.getNullableDependency(clazz)
			?: findInChainScopes(
				scopeName = scopeName,
				clazz = clazz
			)
			?: throw SimplyDINotFoundException(String.format(NOT_FOUND_ERROR, clazz))
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
		logger.d(TAG, "$GET_DEP_FACTORY${clazz} ")
		return mapContainers[scopeName]?.getFactoryDependency(clazz = clazz)
			?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
	}

	@Suppress("UNCHECKED_CAST")
	@Throws(SimplyDINotFoundException::class)
	public fun <T: Any> getByClassAnyway(
		scopeName: String = DEFAULT_SCOPE_NAME,
		clazz: KClass<*>,
	): T {
		logger.d(TAG, "$GET_DEP_FACTORY_WITH_ERROR${clazz}")
		val scope = mapContainers[scopeName] ?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
		return scope.getByClass(clazz)
			?: mapContainers.asSequence()
				.filter { entry -> entry.value.isSearchInScope }
				.mapNotNull { diScopeEntry -> diScopeEntry.value.getByClass(clazz) }
				.firstOrNull() as? T
			?: findInChainScopes(
				scopeName = scopeName,
				clazz = clazz
			)
			?: throw SimplyDINotFoundException(String.format(NOT_FOUND_ERROR, clazz))
	}

	@Throws(SimplyDINotFoundException::class)
	public fun <T: Any> getByClass(
		scopeName: String = DEFAULT_SCOPE_NAME,
		clazz: KClass<*>,
	): T? {
		logger.d(TAG, "$GET_DEP_FACTORY_NULLABLE${clazz}")
		val scope = mapContainers[scopeName] ?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
		return scope.getByClass(clazz)
	}

	internal fun addChainScopes(
		listOfScopes: List<String>
	): Unit = synchronized(this) {
		logger.d(TAG, String.format(LOG_INIT_CHAIN, listOfScopes.joinToString(prefix = "\"", separator = "\", \"", postfix = "\"")))
		listOfScopes.forEach { scopeName ->
			val scope = chainedScopes[scopeName] ?: mutableListOf()
			scope.add(listOfScopes)
			chainedScopes[scopeName] = scope
		}
	}

	internal fun deleteChainedScopes(
		listOfScopes: List<String>
	): Unit = synchronized(this) {
		logger.d(TAG, String.format(LOG_DELETE_CHAIN, listOfScopes.joinToString(prefix = "\"", separator = "\", \"", postfix = "\"")))
		listOfScopes.forEach { scopeName ->
			chainedScopes[scopeName]?.remove(listOfScopes)
		}
	}

	private fun isDependencyInScope(
		clazz: KClass<*>,
		scopeName: String = DEFAULT_SCOPE_NAME
	): Boolean = mapContainers[scopeName]?.isDependencyInScope(clazz = clazz) == true

	private fun <T : Any> findInChainScopes(
		clazz: KClass<*>,
		scopeName: String = DEFAULT_SCOPE_NAME,
	): T? {
		chainedScopes[scopeName]?.forEach { chainedScopes ->
			chainedScopes.forEach { chainedName ->
				if (scopeName != chainedName){
					mapContainers[chainedName]?.getNullableDependency<T>(clazz = clazz)?.let { dependency ->
						return dependency
					}
				}
			}
		}
		return null
	}

	@Suppress("UNCHECKED_CAST")
	@OptIn(ExperimentalTime::class)
	public fun <T: Any> depBenchmark(
		clazz: KClass<*>,
	) {
		val mainTimes = 10
		val times = 100
		var mainsyncTimer = 0L
		var mainusuTimer = 0L
		var mainseqTimer = 0L
		thread {
			repeat(mainTimes) {
				var syncTimer = 0L
				var usuTimer = 0L
				var seqTimer = 0L

				repeat(times) {
					seqTimer += measureTime {
						mapContainers.asSequence()
							.filter { entry -> entry.value.isSearchInScope }
							.mapNotNull { diScopeEntry -> diScopeEntry.value.getByClass(clazz) }
							.firstOrNull() as? T
					}.inWholeMicroseconds



					usuTimer += measureTime {
						mapContainers
							.filter { entry -> entry.value.isSearchInScope }
							.mapNotNull { diScopeEntry -> diScopeEntry.value.getByClass(clazz) }
							.firstOrNull() as? T
					}.inWholeMicroseconds
				}
				repeat(times){
					syncTimer += measureTime {
						getByClassAnyway(clazz = clazz)
					}.inWholeMicroseconds
				}
				mainusuTimer += usuTimer
				mainseqTimer += seqTimer
				mainsyncTimer += syncTimer
				logger.wtf(TAG, "asSequence -  ${seqTimer / times} μs")
				logger.wtf(TAG, "usualArray -  ${usuTimer / times} μs")
				logger.wtf(TAG, "syncTimer -  ${syncTimer / times} μs")
			}
			logger.wtf(TAG, "Main asSequence -  ${mainseqTimer/(mainTimes *times)} μs")
			logger.wtf(TAG, "Main usualArray -  ${mainusuTimer/(mainTimes *times)} μs")
			logger.wtf(TAG, "Main syncTimer -  ${mainsyncTimer/(mainTimes *times)} μs")
		}
	}

	public companion object {
		private const val TAG = "SIMPLY DI CONTAINER"

		private const val LOG_INIT = "Scope with name - %s has been initialized"
		private const val LOG_INIT_CHAIN = "Created a chain with scopes - %s"
		private const val LOG_DELETE_CHAIN = "Deleted a chain with scopes - %s"
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
		private const val DELETE_DEP_ERR = "You try to delete dependency in not created scope"
		private const val GET_DEP_SINGLE = "Запрошена зависимость с добавлением в контейнер - "
		private const val GET_DEP_FACTORY = "Запрошена зависимость без добавления - "
		private const val GET_DEP_FACTORY_WITH_ERROR = "Запрошена зависимость без добавления с ошибкой - "
		private const val GET_DEP_FACTORY_NULLABLE = "Запрошена зависимость без добавления нулабельно - "
		private const val NOT_FOUND_ERROR = "In the beginning, you need to register such a service - %s, before calling it"
		private const val REPLACE_ERR = "You want to replace - \"%s\" in scope \"%s\"?\nPls try the methods - \"replaceNow\"|\"replaceLater\"."
		public val instance: SimplyDIContainer = SimplyDIContainer()
	}
}