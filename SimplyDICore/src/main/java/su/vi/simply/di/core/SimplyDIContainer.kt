package su.vi.simply.di.core

import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.lazy.SimplyDILazyWrapper
import su.vi.simply.di.core.utils.SimplyDIConstants.CREATE_DEP_IMMEDIATELY
import su.vi.simply.di.core.utils.SimplyDIConstants.CREATE_DEP_LAZY
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.core.utils.SimplyDIConstants.DELETE_DEP
import su.vi.simply.di.core.utils.SimplyDIConstants.DELETE_DEP_ERR
import su.vi.simply.di.core.utils.SimplyDIConstants.GET_DEP_FACTORY
import su.vi.simply.di.core.utils.SimplyDIConstants.GET_DEP_FACTORY_WITH_ERROR
import su.vi.simply.di.core.utils.SimplyDIConstants.GET_DEP_SINGLE
import su.vi.simply.di.core.utils.SimplyDIConstants.GET_DEP_SINGLE_LAZY
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
import su.vi.simply.di.core.utils.toLogCallback
import kotlin.concurrent.thread
import kotlin.reflect.KClass
import kotlin.synchronized
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

public class SimplyDIContainer(
    public val scopeName: String = DEFAULT_SCOPE_NAME,
    public val isSearchInScope: Boolean = false,
) {
    private var onLog: LogCallback? = null
    private val registry = ScopeRegistry()

    internal fun initialize(
        scopeName: String = DEFAULT_SCOPE_NAME,
        simplyLogLevel: SimplyLogLevel = SimplyLogLevel.EMPTY,
        isSearchInScope: Boolean = true,
    ): Unit = synchronized(this) {
        onLog = simplyLogLevel.toLogCallback()
        when {
            registry.get(scopeName) != null && scopeName != DEFAULT_SCOPE_NAME -> {
                onLog?.invoke(LogLevel.ERROR, TAG, String.format(LOG_INIT_ALREADY, scopeName))
                return
            }
            registry.get(scopeName) != null && scopeName == DEFAULT_SCOPE_NAME -> return
        }
        registry.create(scopeName, isSearchInScope)
        onLog?.invoke(LogLevel.DEBUG, TAG, String.format(LOG_INIT, scopeName))
    }

    internal fun <T : Any> addDependencyNow(
        scopeName: String = DEFAULT_SCOPE_NAME,
        kClass: KClass<*>,
        factory: () -> T,
    ): Unit = synchronized(this) {
        if (isDependencyInScope(scopeName, kClass)) {
            onLog?.invoke(LogLevel.ERROR, TAG, String.format(REPLACE_ERR, kClass, scopeName))
            return
        }
        registry.get(scopeName)?.createDependencyNow(kClass, factory)
            ?: throw SimplyDINotFoundException(String.format(TRY_TO_CREATE_DEP_WHEN_SCOPE_IS_NOT_CREATED, kClass, scopeName))
        onLog?.invoke(LogLevel.DEBUG, TAG, "$CREATE_DEP_IMMEDIATELY$kClass")
    }

    internal fun <T : Any> addDependencyLater(
        scopeName: String = DEFAULT_SCOPE_NAME,
        kClass: KClass<*>,
        factory: () -> T,
    ): Unit = synchronized(this) {
        if (isDependencyInScope(scopeName, kClass)) {
            onLog?.invoke(LogLevel.ERROR, TAG, String.format(REPLACE_ERR, kClass, scopeName))
            return
        }
        registry.get(scopeName)?.createDependencyLater(kClass, factory)
            ?: throw SimplyDINotFoundException(String.format(TRY_TO_CREATE_DEP_WHEN_SCOPE_IS_NOT_CREATED, kClass, scopeName))
        onLog?.invoke(LogLevel.DEBUG, TAG, "$CREATE_DEP_LAZY$kClass")
    }

    internal fun <T : Any> replaceDependencyNow(
        scopeName: String = DEFAULT_SCOPE_NAME,
        kClass: KClass<*>,
        factory: () -> T,
    ) {
        deleteDependency(scopeName = scopeName, kClass = kClass)
        addDependencyNow(scopeName = scopeName, kClass = kClass, factory = factory)
    }

    internal fun <T : Any> replaceDependencyLater(
        scopeName: String = DEFAULT_SCOPE_NAME,
        kClass: KClass<*>,
        factory: () -> T,
    ) {
        deleteDependency(scopeName = scopeName, kClass = kClass)
        addDependencyLater(scopeName = scopeName, kClass = kClass, factory = factory)
    }

    internal fun deleteDependency(
        scopeName: String = DEFAULT_SCOPE_NAME,
        kClass: KClass<*>,
    ): Unit = synchronized(this) {
        registry.get(scopeName)?.let {
            it.delete(kClass)
            onLog?.invoke(LogLevel.DEBUG, TAG, "$DELETE_DEP$kClass")
            return
        }
        onLog?.invoke(LogLevel.DEBUG, TAG, "$DELETE_DEP_ERR$kClass")
    }

    @Throws(SimplyDINotFoundException::class)
    internal fun <T : Any> getDependency(
        kClass: KClass<*>,
        scopeName: String = DEFAULT_SCOPE_NAME,
    ): T {
        onLog?.invoke(LogLevel.DEBUG, TAG, "$GET_DEP_SINGLE$kClass")
        val scope = registry.get(scopeName)
            ?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
        return scope.getNullableDependency(kClass)
            ?: registry.findInChains(scopeName, kClass)
            ?: throw SimplyDINotFoundException(String.format(NOT_FOUND_ERROR, kClass))
    }

    @Throws(SimplyDINotFoundException::class)
    internal fun <T : Any> getDependencyByLazy(
        scopeName: String = DEFAULT_SCOPE_NAME,
        kClass: KClass<*>,
    ): SimplyDILazyWrapper<T> {
        onLog?.invoke(LogLevel.DEBUG, TAG, "$GET_DEP_SINGLE_LAZY$kClass")
        return SimplyDILazyWrapper { getDependency(kClass, scopeName) }
    }

    @Throws(SimplyDINotFoundException::class)
    internal fun <T : Any> getFactoryDependency(
        scopeName: String = DEFAULT_SCOPE_NAME,
        kClass: KClass<*>,
    ): T {
        onLog?.invoke(LogLevel.DEBUG, TAG, "$GET_DEP_FACTORY$kClass")
        return registry.get(scopeName)
            ?.getFactoryDependency(kClass)
            ?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(SimplyDINotFoundException::class)
    internal fun <T : Any> getByClassAnyway(
        scopeName: String = DEFAULT_SCOPE_NAME,
        kClass: KClass<*>,
    ): T {
        onLog?.invoke(LogLevel.DEBUG, TAG, "$GET_DEP_FACTORY_WITH_ERROR$kClass")
        val scope = registry.get(scopeName)
            ?: throw SimplyDINotFoundException(SCOPE_IS_NOT_INITIALIZED)
        return scope.getNullableDependency(kClass)
            ?: scope.getByClass(kClass)
            ?: registry.allScopes()
                .asSequence()
                .filter { it.value.isSearchInScope }
                .mapNotNull { it.value.getNullableDependency(kClass) }
                .firstOrNull() as? T
            ?: registry.findInChains(scopeName, kClass)
            ?: throw SimplyDINotFoundException(String.format(NOT_FOUND_ERROR, kClass))
    }

    internal fun addChainScopes(listOfScopes: List<String>): Unit = synchronized(this) {
        onLog?.invoke(LogLevel.DEBUG, TAG, String.format(LOG_INIT_CHAIN, listOfScopes.logString()))
        registry.addChains(listOfScopes)
    }

    internal fun deleteChainedScopes(listOfScopes: List<String>): Unit = synchronized(this) {
        onLog?.invoke(LogLevel.DEBUG, TAG, String.format(LOG_DELETE_CHAIN, listOfScopes.logString()))
        registry.removeChains(listOfScopes)
    }

    /** Close a scope — clear all cached instances and factories. */
    internal fun closeScope(scopeName: String): List<String> = synchronized(this) {
        val scope = registry.destroyScope(scopeName) ?: return@synchronized emptyList()
        onLog?.invoke(LogLevel.DEBUG, TAG, "Scope $scopeName closed")
        listOf(scopeName)
    }

    /** Create a new scope within this container. */
    internal fun createScope(name: String, isSearchInScope: Boolean = true): Unit = synchronized(this) {
        if (registry.get(name) != null) {
            onLog?.invoke(LogLevel.ERROR, TAG, String.format(LOG_INIT_ALREADY, name))
            return
        }
        registry.create(name, isSearchInScope)
        onLog?.invoke(LogLevel.DEBUG, TAG, String.format(LOG_INIT, name))
    }

    @Suppress("UNCHECKED_CAST")
    @OptIn(ExperimentalTime::class)
    internal fun <T : Any> depBenchmark(kClass: KClass<*>) {
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
                        registry.allScopes()
                            .asSequence()
                            .filter { it.value.isSearchInScope }
                            .mapNotNull { it.value.getByClass(kClass) }
                            .firstOrNull() as? T
                    }.inWholeMicroseconds

                    usuTimer += measureTime {
                        registry.allScopes()
                            .filter { it.value.isSearchInScope }
                            .mapNotNull { it.value.getByClass(kClass) }
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
                onLog?.invoke(LogLevel.WTF, TAG, "asSequence -  ${seqTimer / times} μs")
                onLog?.invoke(LogLevel.WTF, TAG, "usualArray -  ${usuTimer / times} μs")
                onLog?.invoke(LogLevel.WTF, TAG, "syncTimer -  ${syncTimer / times} μs")
            }
            onLog?.invoke(LogLevel.WTF, TAG, "Main asSequence -  ${mainseqTimer / (mainTimes * times)} μs")
            onLog?.invoke(LogLevel.WTF, TAG, "Main usualArray -  ${mainusuTimer / (mainTimes * times)} μs")
            onLog?.invoke(LogLevel.WTF, TAG, "Main syncTimer -  ${mainsyncTimer / (mainTimes * times)} μs")
        }
    }

    private fun isDependencyInScope(scopeName: String = DEFAULT_SCOPE_NAME, kClass: KClass<*>): Boolean =
        registry.get(scopeName)?.isDependencyInScope(kClass) == true

    private fun List<String>.logString() =
        joinToString(prefix = "\"", separator = "\", \"", postfix = "\"")

    public companion object {
        private var _instance: SimplyDIContainer? = null
        public val instance: SimplyDIContainer
            get() = _instance ?: synchronized(this) {
                _instance ?: SimplyDIContainer().also {
                    it.initialize()
                    _instance = it
                }
            }
    }
}
