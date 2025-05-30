package su.vi.simply.di.core

import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.lazy.SimplyDILazyWrapper
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

	/**
	 * Method for getting a dependency from [SimplyDIScope] immediately.
	 * @return T
	 * @throws SimplyDINotFoundException if dependency is not found
	 **/
	@Throws(SimplyDINotFoundException::class)
	internal fun <T : Any> addDependencyNow(
		scopeName: String = DEFAULT_SCOPE_NAME,
		kClass: KClass<*>,
		factory: () -> T,
	): Unit = synchronized(this) {
		if (isDependencyInScope(scopeName = scopeName, kClass = kClass)) {
			logger.e(TAG, String.format(REPLACE_ERR, kClass, scopeName))
			return@synchronized
		}
		mapContainers[scopeName]?.createDependencyNow(
			kClass = kClass,
			factory = factory
		) ?: kotlin.run {
			throw SimplyDINotFoundException(
				String.format(TRY_TO_CREATE_DEP_WHEN_SCOPE_IS_NOT_CREATED, kClass, scopeName)
			)
		}
		logger.d(TAG, "$CREATE_DEP_IMMEDIATELY${kClass}")
	}

	/**
	 * Method for getting a dependency from [SimplyDIScope].
	 * @return T
	 * @throws SimplyDINotFoundException if dependency is not found
	 **/
	@Throws(SimplyDINotFoundException::class)
	internal fun <T : Any> addDependencyLater(
		scopeName: String = DEFAULT_SCOPE_NAME,
		kClass: KClass<*>,
		factory: () -> T,
	): Unit = synchronized(this) {
		if (isDependencyInScope(scopeName = scopeName, kClass = kClass)) {
			logger.e(TAG, String.format(REPLACE_ERR, kClass, scopeName))
			return@synchronized
		}
		mapContainers[scopeName]?.createDependencyLater(
			kClass = kClass,
			factory = factory
		) ?: kotlin.run {
			throw SimplyDINotFoundException(
				String.format(TRY_TO_CREATE_DEP_WHEN_SCOPE_IS_NOT_CREATED, kClass, scopeName)
			)
		}
		logger.d(TAG, "$CREATE_DEP_LAZY${kClass}")
	}

	internal fun <T : Any> replaceDependencyNow(
		scopeName: String = DEFAULT_SCOPE_NAME,
		kClass: KClass<*>,
		factory: () -> T,
	) {
		deleteDependency(kClass = kClass, scopeName = scopeName)
		addDependencyNow(scopeName = scopeName, kClass = kClass, factory = factory)
	}

	internal fun <T : Any> replaceDependencyLater(
		scopeName: String = DEFAULT_SCOPE_NAME,
		kClass: KClass<*>,
		factory: () -> T,
	) {
		deleteDependency(kClass = kClass, scopeName = scopeName)
		addDependencyLater(scopeName = scopeName, kClass = kClass, factory = factory)
	}

	/**
	 * Method for removing dependencies from initialized [SimplyDIScope] and a constructor for initialization.
	 **/
	internal fun deleteDependency(
		scopeName: String = DEFAULT_SCOPE_NAME,
		kClass: KClass<*>,
	): Unit = synchronized(this) {
		mapContainers[scopeName]?.apply {
			delete(kClass)
			logger.d(TAG, "$DELETE_DEP${kClass}")
			return@synchronized
		}
		logger.d(TAG, "$DELETE_DEP_ERR${kClass}")
	}

	/**
	 * A method for getting a dependency from [SimplyDIScope].
	 * @return T
	 * @throws SimplyDINotFoundException if the dependency is not found when added to the container
	 **/
	@Throws(SimplyDINotFoundException::class)
	internal fun <T : Any> getDependency(
		kClass: KClass<*>,
		scopeName: String = DEFAULT_SCOPE_NAME,
	): T {
		logger.d(TAG, "$GET_DEP_SINGLE${kClass}")
		val scope = mapContainers[scopeName] ?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
		return scope.getNullableDependency(kClass)
			?: findInChainScopes(
				scopeName = scopeName,
				kClass = kClass
			)
			?: throw SimplyDINotFoundException(String.format(NOT_FOUND_ERROR, kClass))
	}

	/**
	 * A method for getting a dependency by lazy from [SimplyDIScope].
	 * @return T
	 * @throws SimplyDINotFoundException if the dependency is not found when added to the container
	 **/
	@Throws(SimplyDINotFoundException::class)
	internal fun <T : Any> getDependencyByLazy(
		scopeName: String = DEFAULT_SCOPE_NAME,
		kClass: KClass<*>,
	): SimplyDILazyWrapper<T> {
		logger.d(TAG, "$GET_DEP_SINGLE${kClass}")
		val scope = mapContainers[scopeName] ?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
		val dependency = scope.getNullableDependency<T>(kClass)
			?: findInChainScopes(
				scopeName = scopeName,
				kClass = kClass
			)
			?: throw SimplyDINotFoundException(String.format(NOT_FOUND_ERROR, kClass))

		return SimplyDILazyWrapper(
			lazyValue = {
				dependency
			}
		)
	}

	/**
	 * A method for getting a dependency from [SimplyDIScope].
	 * @return T
	 * @throws SimplyDINotFoundException if the dependency is not found without being added to the container
	 **/
	@Throws(SimplyDINotFoundException::class)
	internal fun <T : Any> getFactoryDependency(
		scopeName: String = DEFAULT_SCOPE_NAME,
		kClass: KClass<*>,
	): T {
		logger.d(TAG, "$GET_DEP_FACTORY${kClass} ")
		return mapContainers[scopeName]?.getFactoryDependency(kClass = kClass)
			?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
	}

	@Suppress("UNCHECKED_CAST")
	@Throws(SimplyDINotFoundException::class)
	internal fun <T : Any> getByClassAnyway(
		scopeName: String = DEFAULT_SCOPE_NAME,
		kClass: KClass<*>,
	): T {
		logger.d(TAG, "$GET_DEP_FACTORY_WITH_ERROR${kClass}")
		val scope = mapContainers[scopeName] ?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
		return scope.getByClass(kClass)
			?: mapContainers.asSequence()
				.filter { entry -> entry.value.isSearchInScope }
				.mapNotNull { diScopeEntry -> diScopeEntry.value.getByClass(kClass) }
				.firstOrNull() as? T
			?: findInChainScopes(
				scopeName = scopeName,
				kClass = kClass
			)
			?: throw SimplyDINotFoundException(String.format(NOT_FOUND_ERROR, kClass))
	}

	@Throws(SimplyDINotFoundException::class)
	internal fun <T : Any> getByClass(
		scopeName: String = DEFAULT_SCOPE_NAME,
		kClass: KClass<*>,
	): T? {
		logger.d(TAG, "$GET_DEP_FACTORY_NULLABLE${kClass}")
		val scope = mapContainers[scopeName] ?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
		return scope.getByClass(kClass)
	}

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
		scopeName: String = DEFAULT_SCOPE_NAME,
		kClass: KClass<*>,
	): Boolean = mapContainers[scopeName]?.isDependencyInScope(kClass = kClass) == true

	private fun <T : Any> findInChainScopes(
		scopeName: String = DEFAULT_SCOPE_NAME,
		kClass: KClass<*>,
	): T? {
		chainedScopes[scopeName]?.forEach { chainedScopes ->
			chainedScopes.forEach { chainedName ->
				if (scopeName != chainedName) {
					mapContainers[chainedName]?.getNullableDependency<T>(kClass = kClass)?.let { dependency ->
						return dependency
					}
				}
			}
		}
		return null
	}

	@Suppress("UNCHECKED_CAST")
	@OptIn(ExperimentalTime::class)
	internal fun <T : Any> depBenchmark(
		kClass: KClass<*>,
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
							.mapNotNull { diScopeEntry -> diScopeEntry.value.getByClass(kClass) }
							.firstOrNull() as? T
					}.inWholeMicroseconds



					usuTimer += measureTime {
						mapContainers
							.filter { entry -> entry.value.isSearchInScope }
							.mapNotNull { diScopeEntry -> diScopeEntry.value.getByClass(kClass) }
							.firstOrNull() as? T
					}.inWholeMicroseconds
				}
				repeat(times) {
					syncTimer += measureTime {
						getByClassAnyway(kClass = kClass)
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

	private fun List<String>.logString() =
		joinToString(prefix = PRE_POST_QUOTES, separator = MIDDLE_QUOTES, postfix = PRE_POST_QUOTES)

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