package su.vi.kdi.core

import su.vi.kdi.Lifecycle
import su.vi.kdi.core.entry_point.KDIContainerDSL
import su.vi.kdi.core.error.KDINotFoundException
import su.vi.kdi.core.lazy.KDILazyWrapper
import su.vi.kdi.core.utils.KDIConstants.CREATE_DEP_LAZY
import su.vi.kdi.core.utils.KDIConstants.DEFAULT_SCOPE_NAME
import su.vi.kdi.core.utils.KDIConstants.GET_DEP_FACTORY_WITH_ERROR
import su.vi.kdi.core.utils.KDIConstants.GET_DEP_SINGLE
import su.vi.kdi.core.utils.KDIConstants.LOG_DELETE_CHAIN
import su.vi.kdi.core.utils.KDIConstants.LOG_INIT
import su.vi.kdi.core.utils.KDIConstants.LOG_INIT_ALREADY
import su.vi.kdi.core.utils.KDIConstants.LOG_INIT_CHAIN
import su.vi.kdi.core.utils.KDIConstants.NOT_FOUND_ERROR
import su.vi.kdi.core.utils.KDIConstants.REPLACE_ERR
import su.vi.kdi.core.utils.KDIConstants.SCOPE_IS_NOT_INITIALIZED
import su.vi.kdi.core.utils.KDIConstants.TAG
import su.vi.kdi.core.utils.KDIConstants.TRY_TO_CREATE_DEP_WHEN_SCOPE_IS_NOT_CREATED
import su.vi.kdi.core.utils.KDIConstants.TRY_TO_RELOAD_CONTAINER
import su.vi.kdi.core.utils.KDILogLevel
import su.vi.kdi.core.utils.toKDILogger
import kotlin.reflect.KClass

/**
 * Container with ScopesStorage, needed for get dependency through project.
 */
public class KDIContainer(
	public val scopeName: String,
	public val isSearchInScope: Boolean,
) {
	private var logger: KDILogger = KDILoggerEmpty()

	/**
	 * Initialize method for new container.
	 * @param scopeName name of the new container.
	 * @param KDILogLevel where you can set level of logs for you needs for example for release
	 * would do use [KDILogLevel.Empty] for debug [KDILogLevel.Full].
	 * @param isSearchInScope if you want to use this container like data store or you need
	 * to share dependencies from this container you would set value like true or you can bind them [KDIContainer.addChainScopes] .
	 */
	internal fun initialize(
		scopeName: String = DEFAULT_SCOPE_NAME,
		kdiLogLevel: KDILogLevel = KDILogLevel.Empty,
		isSearchInScope: Boolean = true,
		dslBuilder: KDIContainerDSL?,
	): Unit = synchronized(this) {
		logger = kdiLogLevel.toKDILogger()
		when {
			mapContainers.containsKey(scopeName) && scopeName != DEFAULT_SCOPE_NAME -> {
				logger.e(TAG, String.format(LOG_INIT_ALREADY, scopeName))
				return
			}

			mapContainers.containsKey(scopeName) && scopeName == DEFAULT_SCOPE_NAME -> {
				return
			}
		}
		mapContainers[scopeName] = KDIScope(isSearchInScope = isSearchInScope, dslBuilder = dslBuilder)

		logger.d(TAG, String.format(LOG_INIT, scopeName))
	}

	/**
	 * Method for getting a dependency from [KDIScope].
	 * @return T
	 * @throws KDINotFoundException if dependency is not found
	 **/
	@Throws(KDINotFoundException::class)
	internal fun <T : Any> addDependency(
		scopeName: String = DEFAULT_SCOPE_NAME,
		name: String? = null,
		lifecycle: Lifecycle = Lifecycle.SINGLETON,
		kClass: KClass<*>,
		factory: () -> T,
	): Unit = synchronized(this) {
		if (isDependencyInScope(scopeName = scopeName, kClass = kClass, name = name)) {
			logger.e(TAG, String.format(REPLACE_ERR, kClass, scopeName))
			return@synchronized
		}
		mapContainers[scopeName]?.createDependency<T>(
			type = kClass.java,
			name = name,
			lifecycle = lifecycle,
			factory = factory
		) ?: kotlin.run {
			throw KDINotFoundException(
				String.format(TRY_TO_CREATE_DEP_WHEN_SCOPE_IS_NOT_CREATED, kClass, scopeName)
			)
		}
		logger.d(TAG, "$CREATE_DEP_LAZY${kClass}")
	}

	/**
	 * Method for getting a dependency from [KDIScope].
	 * @return T
	 * @throws KDINotFoundException if dependency is not found
	 **/
	@Throws(KDINotFoundException::class)
	internal fun <T : Any> addDependencyLambdaAuto(
		scopeName: String = DEFAULT_SCOPE_NAME,
		name: String? = null,
		kClass: KClass<T>,
	): Unit = synchronized(this) {
		if (isDependencyInScope(scopeName = scopeName, kClass = kClass)) {
			logger.e(TAG, String.format(REPLACE_ERR, kClass, scopeName))
			return@synchronized
		}
		mapContainers[scopeName]?.createDependencyLambdaAuto<T>(
			clazz = kClass.java,
			name = name,
		) ?: kotlin.run {
			throw KDINotFoundException(
				String.format(TRY_TO_CREATE_DEP_WHEN_SCOPE_IS_NOT_CREATED, kClass, scopeName)
			)
		}
		logger.d(TAG, "$CREATE_DEP_LAZY${kClass}")
	}

	/**
	 * Method for getting a dependency from [KDIScope].
	 * @return T
	 * @throws KDINotFoundException if dependency is not found
	 **/
	@Throws(KDINotFoundException::class)
	internal fun <T : Any> addDependencyAuto(
		scopeName: String = DEFAULT_SCOPE_NAME,
		name: String? = null,
		kClass: KClass<T>,
	): Unit = synchronized(this) {
		if (isDependencyInScope(scopeName = scopeName, kClass = kClass)) {
			logger.e(TAG, String.format(REPLACE_ERR, kClass, scopeName))
			return@synchronized
		}
		mapContainers[scopeName]?.createDependencyAuto<T>(
			clazz = kClass.java,
			name = name,
		) ?: kotlin.run {
			throw KDINotFoundException(
				String.format(TRY_TO_CREATE_DEP_WHEN_SCOPE_IS_NOT_CREATED, kClass, scopeName)
			)
		}
		logger.d(TAG, "$CREATE_DEP_LAZY${kClass}")
	}

	/**
	 * Method for getting a dependency from [KDIScope].
	 * @return T
	 * @throws KDINotFoundException if dependency is not found
	 **/
	@Throws(KDINotFoundException::class)
	internal fun <T : Any> addDependencyManually(
		scopeName: String = DEFAULT_SCOPE_NAME,
		name: String? = null,
		kClass: KClass<T>,
		supertypes: List<KClass<*>>,
	): Unit = synchronized(this) {
		if (isDependencyInScope(scopeName = scopeName, kClass = kClass)) {
			logger.e(TAG, String.format(REPLACE_ERR, kClass, scopeName))
			return@synchronized
		}
		mapContainers[scopeName]?.createDependencyManually<T>(
			clazz = kClass.java,
			name = name,
			supertypes = supertypes.map { it.java }
		) ?: kotlin.run {
			throw KDINotFoundException(
				String.format(TRY_TO_CREATE_DEP_WHEN_SCOPE_IS_NOT_CREATED, kClass, scopeName)
			)
		}
		logger.d(TAG, "$CREATE_DEP_LAZY${kClass}")
	}

	/**
	 * A method for getting a dependency from [KDIScope].
	 * @return T
	 * @throws KDINotFoundException if the dependency is not found when added to the container
	 **/
	@Throws(KDINotFoundException::class)
	internal fun <T : Any> getDependency(
		kClass: KClass<T>,
		name: String? = null,
		scopeName: String = DEFAULT_SCOPE_NAME,
	): T {
		logger.d(TAG, "$GET_DEP_SINGLE${kClass}")
		val scope = mapContainers[scopeName] ?: throw KDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
		return scope.getNullableDependency<T>(clazz = kClass.java, name = name)
			?: reInitializeScopeAndCallDependency(scopeName = scopeName) {
				scope.getNullableDependency<T>(clazz = kClass.java, name = name)
			}
			?: findInChainScopes(
				scopeName = scopeName,
				kClass = kClass
			)

			?: throw KDINotFoundException(String.format(NOT_FOUND_ERROR, kClass, name))
	}

	/**
	 * A method for getting a dependency by lazy from [KDIScope].
	 * @return T
	 * @throws KDINotFoundException if the dependency is not found when added to the container
	 **/
	@Throws(KDINotFoundException::class)
	internal fun <T : Any> getDependencyByLazy(
		scopeName: String = DEFAULT_SCOPE_NAME,
		name: String? = null,
		kClass: KClass<T>,
	): KDILazyWrapper<T> {
		logger.d(TAG, "$GET_DEP_SINGLE${kClass}")
		val scope = mapContainers[scopeName] ?: throw KDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
		val dependency = scope.getNullableDependency<T>(clazz = kClass.java, name = name)
			?: reInitializeScopeAndCallDependency(scopeName = scopeName) {
				scope.getNullableDependency<T>(clazz = kClass.java, name = name)
			}
			?: findInChainScopes(
				scopeName = scopeName,
				kClass = kClass
			)
			?: throw KDINotFoundException(String.format(NOT_FOUND_ERROR, kClass, name))

		return KDILazyWrapper(
			lazyValue = {
				dependency
			}
		)
	}

	@Suppress("UNCHECKED_CAST")
	@Throws(KDINotFoundException::class)
	internal fun <T : Any> getByClassAnyway(
		scopeName: String = DEFAULT_SCOPE_NAME,
		name: String? = null,
		kClass: KClass<T>,
	): T {
		logger.d(TAG, "$GET_DEP_FACTORY_WITH_ERROR${kClass}")
		val scope = mapContainers[scopeName] ?: throw KDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
		return scope.getNullableDependency<T>(clazz = kClass.java, name = name)
			?: mapContainers.asSequence()
				.filter { entry -> entry.value.isSearchInScope }
				.mapNotNull { diScopeEntry ->
					diScopeEntry.value.getNullableDependency<T>(
						clazz = kClass.java,
						name = name
					)
				}
				.firstOrNull() as? T
			?: reInitializeScopeAndCallDependency(scopeName = scopeName) {
				scope.getNullableDependency<T>(clazz = kClass.java, name = name)
			}
			?: findInChainScopes(
				scopeName = scopeName,
				kClass = kClass
			)
			?: throw KDINotFoundException(String.format(NOT_FOUND_ERROR, kClass, name))
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

	private fun <T : Any> reInitializeScopeAndCallDependency(
		scopeName: String = DEFAULT_SCOPE_NAME,
		callDependency: () -> T?,
	): T? {
		val container = mapContainers[scopeName]
		if (container != null && !container.isInitialized) {
			container.isInitialized = true
			val dslBuilder = container.dslBuilder
			if (dslBuilder == null) {
				return null
			}
			logger.d(TAG, "$TRY_TO_RELOAD_CONTAINER${scopeName}")
			dslBuilder.builder.invoke(dslBuilder)
		} else {
			return null
		}
		return callDependency.invoke()
	}

	private fun isDependencyInScope(
		scopeName: String = DEFAULT_SCOPE_NAME,
		name: String? = null,
		kClass: KClass<*>,
	): Boolean = mapContainers[scopeName]?.isDependencyInScope(kClass = kClass, name = name) == true

	private fun <T : Any> findInChainScopes(
		scopeName: String = DEFAULT_SCOPE_NAME,
		name: String? = null,
		kClass: KClass<T>,
	): T? {
		chainedScopes[scopeName]?.forEach { chainedScopes ->
			chainedScopes.forEach { chainedName ->
				if (scopeName != chainedName) {
					val container = mapContainers[chainedName]
					val dependency = container?.getNullableDependency<T>(clazz = kClass.java, name = name)
						?: reInitializeScopeAndCallDependency(scopeName = chainedName) {
							container?.getNullableDependency<T>(clazz = kClass.java, name = name)
						}
					if (dependency != null) {
						return dependency
					}
				}
			}
		}
		return null
	}

	private fun List<String>.logString() =
		joinToString(prefix = PRE_POST_QUOTES, separator = MIDDLE_QUOTES, postfix = PRE_POST_QUOTES)

	public companion object {
		private const val PRE_POST_QUOTES = "\""
		private const val MIDDLE_QUOTES = "$PRE_POST_QUOTES, $PRE_POST_QUOTES"

		private val mapContainers = mutableMapOf<String, KDIScope>()
		private val chainedScopes = mutableMapOf<String, MutableList<List<String>>>()
		public val instance: KDIContainer by lazy {
			KDIContainer(scopeName = DEFAULT_SCOPE_NAME, isSearchInScope = true)
		}
	}
}
