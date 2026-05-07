package su.vi.simply.di.core

import su.vi.simply.di.core.error.SimplyDINotFoundException
import kotlin.reflect.KClass

internal class SimplyDIScope(
    val isSearchInScope: Boolean,
) {

    private val initializerFactory: MutableMap<Any, () -> Any> = mutableMapOf()
    private val listOfDependencies: MutableMap<Any, Any> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    internal fun <T : Any> getNullableDependency(kClass: KClass<*>): T? = synchronized(this) {
        listOfDependencies[kClass] ?: run {
            val newInstance = initializerFactory[kClass]?.invoke()
                ?: return@synchronized null
            listOfDependencies[kClass] = newInstance
            listOfDependencies[newInstance] = newInstance
            newInstance
        }
    } as? T

    @Suppress("UNCHECKED_CAST")
    internal fun <T : Any> getFactoryDependency(kClass: KClass<*>): T =
        initializerFactory[kClass]?.invoke() as? T
            ?: throw SimplyDINotFoundException("Factory not found for $kClass")

    @Suppress("UNCHECKED_CAST")
    internal fun <T : Any> getByClass(kClass: KClass<*>): T? =
        initializerFactory[kClass]?.invoke() as? T

    internal fun <T : Any> createDependencyLater(kClass: KClass<*>, factory: () -> T) {
        initializerFactory[kClass] = factory
    }

    internal fun <T : Any> createDependencyNow(kClass: KClass<*>, factory: () -> T) {
        initializerFactory[kClass] = factory
        listOfDependencies[kClass] = factory.invoke()
    }

    internal fun delete(kClass: KClass<*>) {
        initializerFactory.remove(kClass)
        listOfDependencies.remove(kClass)
    }

    internal fun isDependencyInScope(kClass: KClass<*>): Boolean =
        initializerFactory.containsKey(kClass) || listOfDependencies.containsKey(kClass)

    /** Clear all cached instances and factories. */
    internal fun close() {
        initializerFactory.clear()
        listOfDependencies.clear()
    }
}
