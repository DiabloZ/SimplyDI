package su.vi.simply.di.core

import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.utils.SimplyDIConstants.CREATE_DEP_IMMEDIATELY
import su.vi.simply.di.core.utils.SimplyDIConstants.CREATE_DEP_LAZY
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.core.utils.SimplyDIConstants.DELETE_DEP
import su.vi.simply.di.core.utils.SimplyDIConstants.DELETE_DEP_ERR
import su.vi.simply.di.core.utils.SimplyDIConstants.GET_DEP_FACTORY
import su.vi.simply.di.core.utils.SimplyDIConstants.GET_DEP_FACTORY_NULLABLE
import su.vi.simply.di.core.utils.SimplyDIConstants.GET_DEP_FACTORY_WITH_ERROR
import su.vi.simply.di.core.utils.SimplyDIConstants.GET_DEP_SINGLE
import su.vi.simply.di.core.utils.SimplyDIConstants.LOG_DELETE_CHAIN
import su.vi.simply.di.core.utils.SimplyDIConstants.LOG_INIT
import su.vi.simply.di.core.utils.SimplyDIConstants.LOG_INIT_ALREADY
import su.vi.simply.di.core.utils.SimplyDIConstants.LOG_INIT_CHAIN
import su.vi.simply.di.core.utils.SimplyDIConstants.NOT_FOUND_ERROR
import su.vi.simply.di.core.utils.SimplyDIConstants.REPLACE_ERR
import su.vi.simply.di.core.utils.SimplyDIConstants.SCOPE_IS_NOT_INITIALIZED
import su.vi.simply.di.core.utils.SimplyDIConstants.TAG
import su.vi.simply.di.core.utils.SimplyDIConstants.TRY_TO_CREATE_DEP_WHEN_SCOPE_IS_NOT_CREATED
import su.vi.simply.di.core.utils.SimplyLogLevel
import su.vi.simply.di.core.utils.addChainScopes
import su.vi.simply.di.core.utils.toSimplyDILogger
import kotlin.concurrent.thread
import kotlin.reflect.KClass
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * Container with ScopesStorage, needed for get dependency through project.
 */
public class SimplyDIContainer(
	public val scopeName: String,
	public val isSearchInScope: Boolean,
) {
	private var logger: SimplyDILogger = SimplyDILoggerEmpty()

	/**
	 * Initialize method for new container.
	 * @param scopeName name of the new container.
	 * @param simplyLogLevel where you can set level of logs for you needs for example for release
	 * would do use [SimplyLogLevel.EMPTY] for debug [SimplyLogLevel.FULL].
	 * @param isSearchInScope if you want to use this container like data store or you need
	 * to share dependencies from this container you would set value like true or you can bind them [SimplyDIContainer.addChainScopes] .
	 */
	@Deprecated(
		message = "Pls don't use methods directly. It can cause problem with binary compatibility.",
		replaceWith = ReplaceWith(
			expression = "this.initializeContainer(scopeName, simplyLogLevel, isSearchInScope)",
			"su.vi.simply.di.core.utils"
		),
		level = DeprecationLevel.WARNING
	)
	internal fun initialize(
		scopeName: String = DEFAULT_SCOPE_NAME,
		simplyLogLevel: SimplyLogLevel = SimplyLogLevel.EMPTY,
		isSearchInScope: Boolean = true,
	): Unit = synchronized(this) {
		logger = simplyLogLevel.toSimplyDILogger()
		when {
			mapContainers.containsKey(scopeName) && scopeName != DEFAULT_SCOPE_NAME -> {
				logger.e(TAG, String.format(LOG_INIT_ALREADY, scopeName))
				return
			}

			mapContainers.containsKey(scopeName) && scopeName == DEFAULT_SCOPE_NAME -> {
				return
			}
		}
		mapContainers[scopeName] = SimplyDIScope(isSearchInScope = isSearchInScope)

		logger.d(TAG, String.format(LOG_INIT, scopeName))
	}

	@Deprecated(
		message = "Pls don't use methods directly. It can cause problem with binary compatibility.",
		replaceWith = ReplaceWith(
			expression = "this.addDependencyNow(scopeName, clazz, factory)",
			"su.vi.simply.di.core.utils"
		),
		level = DeprecationLevel.WARNING
	)
	@Throws(SimplyDINotFoundException::class)
	public fun <T : Any> addDependencyNow(
		scopeName: String = DEFAULT_SCOPE_NAME,
		clazz: KClass<*>,
		factory: () -> T,
	): Unit = synchronized(this) {
		if (isDependencyInScope(scopeName = scopeName, clazz = clazz)) {
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
	@Deprecated(
		message = "Pls don't use methods directly. It can cause problem with binary compatibility.",
		replaceWith = ReplaceWith(
			expression = "this.addDependencyLater<T>(scopeName, factory)",
			"su.vi.simply.di.core.utils"
		),
		level = DeprecationLevel.WARNING
	)
	@Throws(SimplyDINotFoundException::class)
	public fun <T : Any> addDependencyLater(
		scopeName: String = DEFAULT_SCOPE_NAME,
		clazz: KClass<*>,
		factory: () -> T,
	): Unit = synchronized(this) {
		if (isDependencyInScope(scopeName = scopeName, clazz = clazz)) {
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

	@Suppress("DEPRECATION")
	@Deprecated(
		message = "Pls don't use methods directly. It can cause problem with binary compatibility.",
		replaceWith = ReplaceWith(
			expression = "this.replaceDependencyNow<T>(scopeName, factory)",
			"su.vi.simply.di.core.utils"
		),
		level = DeprecationLevel.WARNING
	)
	public fun <T : Any> replaceDependencyNow(
		scopeName: String = DEFAULT_SCOPE_NAME,
		clazz: KClass<*>,
		factory: () -> T,
	) {
		deleteDependency(clazz = clazz, scopeName = scopeName)
		addDependencyNow(scopeName = scopeName, clazz = clazz, factory = factory)
	}

	@Suppress("DEPRECATION")
	@Deprecated(
		message = "Pls don't use methods directly. It can cause problem with binary compatibility.",
		replaceWith = ReplaceWith(
			expression = "this.replaceDependencyLater<T>(scopeName, factory)",
			"su.vi.simply.di.core.utils"
		),
		level = DeprecationLevel.WARNING
	)
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
	@Deprecated(
		message = "Pls don't use methods directly. It can cause problem with binary compatibility.",
		replaceWith = ReplaceWith(
			expression = "this.deleteDependency<T>(scopeName)",
			"su.vi.simply.di.core.utils"
		),
		level = DeprecationLevel.WARNING
	)
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
	@Deprecated(
		message = "Pls don't use methods directly. It can cause problem with binary compatibility.",
		replaceWith = ReplaceWith(
			expression = "this.getDependency<T>(scopeName)",
			"su.vi.simply.di.core.utils"
		),
		level = DeprecationLevel.WARNING
	)
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
	@Deprecated(
		message = "Pls don't use methods directly. It can cause problem with binary compatibility.",
		replaceWith = ReplaceWith(
			expression = "this.getFactoryDependency<T>(scopeName)",
			"su.vi.simply.di.core.utils"
		),
		level = DeprecationLevel.WARNING
	)
	@Throws(SimplyDINotFoundException::class)
	public fun <T : Any> getFactoryDependency(
		clazz: KClass<*>,
		scopeName: String = DEFAULT_SCOPE_NAME,
	): T {
		logger.d(TAG, "$GET_DEP_FACTORY${clazz} ")
		return mapContainers[scopeName]?.getFactoryDependency(clazz = clazz)
			?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
	}

	@Deprecated(
		message = "Pls don't use methods directly. It can cause problem with binary compatibility.",
		replaceWith = ReplaceWith(
			expression = "this.getByClassAnyway<T>(scopeName)",
			"su.vi.simply.di.core.utils"
		),
		level = DeprecationLevel.WARNING
	)
	@Suppress("UNCHECKED_CAST")
	@Throws(SimplyDINotFoundException::class)
	public fun <T : Any> getByClassAnyway(
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

	@Deprecated(
		message = "Pls don't use methods directly. It can cause problem with binary compatibility.",
		replaceWith = ReplaceWith(
			expression = "this.getByClass<T>(scopeName)",
			"su.vi.simply.di.core.utils"
		),
		level = DeprecationLevel.WARNING
	)
	@Throws(SimplyDINotFoundException::class)
	public fun <T : Any> getByClass(
		scopeName: String = DEFAULT_SCOPE_NAME,
		clazz: KClass<*>,
	): T? {
		logger.d(TAG, "$GET_DEP_FACTORY_NULLABLE${clazz}")
		val scope = mapContainers[scopeName] ?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
		return scope.getByClass(clazz)
	}

	@Deprecated(
		message = "Pls don't use methods directly. It can cause problem with binary compatibility.",
		replaceWith = ReplaceWith(
			expression = "this.addChainScopes<T>(listOfScopes)",
			"su.vi.simply.di.core.utils"
		),
		level = DeprecationLevel.WARNING
	)
	internal fun addChainScopes(
		listOfScopes: List<String>,
	): Unit = synchronized(this) {
		logger.d(
			TAG,
			String.format(
				LOG_INIT_CHAIN,
				listOfScopes.logString()
			)
		)
		listOfScopes.forEach { scopeName ->
			val scope = chainedScopes[scopeName] ?: mutableListOf()
			scope.add(listOfScopes)
			chainedScopes[scopeName] = scope
		}
	}

	@Deprecated(
		message = "Pls don't use methods directly. It can cause problem with binary compatibility.",
		replaceWith = ReplaceWith(
			expression = "this.deleteChainedScopes<T>(listOfScopes)",
			"su.vi.simply.di.core.utils"
		),
		level = DeprecationLevel.WARNING
	)
	internal fun deleteChainedScopes(
		listOfScopes: List<String>,
	): Unit = synchronized(this) {
		logger.d(
			TAG,
			String.format(
				LOG_DELETE_CHAIN,
				listOfScopes.logString()
			)
		)
		listOfScopes.forEach { scopeName ->
			chainedScopes[scopeName]?.remove(listOfScopes)
		}
	}

	private fun isDependencyInScope(
		clazz: KClass<*>,
		scopeName: String = DEFAULT_SCOPE_NAME,
	): Boolean = mapContainers[scopeName]?.isDependencyInScope(clazz = clazz) == true

	private fun <T : Any> findInChainScopes(
		clazz: KClass<*>,
		scopeName: String = DEFAULT_SCOPE_NAME,
	): T? {
		chainedScopes[scopeName]?.forEach { chainedScopes ->
			chainedScopes.forEach { chainedName ->
				if (scopeName != chainedName) {
					mapContainers[chainedName]?.getNullableDependency<T>(clazz = clazz)?.let { dependency ->
						return dependency
					}
				}
			}
		}
		return null
	}

	@Suppress("UNCHECKED_CAST", "DEPRECATION")
	@Deprecated(
		message = "Pls don't use methods directly. It can cause problem with binary compatibility.",
		replaceWith = ReplaceWith(
			expression = "this.depBenchmark<T>()",
			"su.vi.simply.di.core.utils"
		),
		level = DeprecationLevel.WARNING
	)
	@OptIn(ExperimentalTime::class)
	public fun <T : Any> depBenchmark(
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
				repeat(times) {
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
			logger.wtf(TAG, "Main asSequence -  ${mainseqTimer / (mainTimes * times)} μs")
			logger.wtf(TAG, "Main usualArray -  ${mainusuTimer / (mainTimes * times)} μs")
			logger.wtf(TAG, "Main syncTimer -  ${mainsyncTimer / (mainTimes * times)} μs")
		}
	}

	private fun List<String>.logString() = joinToString(prefix = PRE_POST_QUOTES, separator = MIDDLE_QUOTES, postfix = PRE_POST_QUOTES)

	public companion object {
		private const val PRE_POST_QUOTES = "\""
		private const val MIDDLE_QUOTES = "$PRE_POST_QUOTES, $PRE_POST_QUOTES"

		private val mapContainers = mutableMapOf<String, SimplyDIScope>()
		private val chainedScopes = mutableMapOf<String, MutableList<List<String>>>()
		public val instance: SimplyDIContainer by lazy {
			SimplyDIContainer(scopeName = DEFAULT_SCOPE_NAME, isSearchInScope = true)
		}
	}
}