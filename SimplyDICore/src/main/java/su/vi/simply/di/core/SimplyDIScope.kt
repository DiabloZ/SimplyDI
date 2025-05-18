package su.vi.simply.di.core

import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.utils.SimplyDIConstants.NOT_FOUND_ERROR
import kotlin.reflect.KClass

/**
 * DI Scope with lazy initialization of dependencies and fabric methods.
 * @param isSearchInScope if you want to use this container like data store or you need
 * to share dependencies from this container you would set value like true or you can bind them [SimplyDIContainer.addChainScopes].
 */
internal class SimplyDIScope(
	val isSearchInScope: Boolean,
) {

	private val initializerFactory: MutableMap<Any, () -> Any> = mutableMapOf()

	private val listOfDependencies: MutableMap<Any, Any> = mutableMapOf()

	/**
	 * Use it to get dependencies with store it in list of dependencies.
	 * If the dependency not created you receive null.
	 * @param kClass T::class of your dependency.
	 * @return T?
	 **/
	@Suppress("UNCHECKED_CAST")
	internal fun <T : Any> getNullableDependency(kClass: KClass<*>): T? {
		val dependency = listOfDependencies[kClass] ?: run {
			val newInstance = initializerFactory[kClass]?.invoke()
				?: return null
			listOfDependencies[kClass] = newInstance
			listOfDependencies[newInstance] = newInstance
			newInstance
		}
		return (dependency as? T)
	}

	/**
	 * Use it to get dependencies with store it in list of dependencies.
	 * @param kClass T::class of your dependency.
	 * @return T
	 * @throws SimplyDINotFoundException if the dependency not created you receive.
	 **/
	@Throws(SimplyDINotFoundException::class)
	internal fun <T : Any> getDependency(kClass: KClass<*>): T {
		return getNullableDependency<T>(kClass = kClass)
			?: throw SimplyDINotFoundException(message = String.format(NOT_FOUND_ERROR, kClass.simpleName))
	}

	/**
	 * Use it to get dependencies without store it in list of dependencies.
	 * @param kClass T::class of your dependency.
	 * @return T
	 * @throws SimplyDINotFoundException if the dependency not created you receive.
	 **/
	@Suppress("UNCHECKED_CAST")
	internal fun <T : Any> getFactoryDependency(kClass: KClass<*>): T {
		return initializerFactory[kClass]?.invoke() as? T
			?: throw SimplyDINotFoundException(message = String.format(NOT_FOUND_ERROR, kClass.simpleName))
	}

	/**
	 * Use it to get dependencies without store it in list of dependencies.
	 * If the dependency not created you receive null.
	 * @param kClass T::class of your dependency.
	 * @return T?
	 **/
	@Suppress("UNCHECKED_CAST")
	internal fun <T : Any> getByClass(
		kClass: KClass<*>,
	): T? {
		return initializerFactory[kClass]?.invoke() as? T
	}

	/**
	 * Use it to get dependencies without store it in list of dependencies.
	 * @param kClass T::class of your dependency.
	 * @return T
	 * @throws SimplyDINotFoundException if the dependency not created you receive.
	 **/
	@Suppress("UNCHECKED_CAST")
	internal fun <T> getByClassAnyway(
		kClass: KClass<*>,
	): T {
		return initializerFactory[kClass]?.invoke() as? T
			?: throw SimplyDINotFoundException(message = String.format(NOT_FOUND_ERROR, kClass.simpleName))
	}

	/**
	 * Use it to create dependency by lazy.
	 **/
	internal fun <T : Any> createDependencyLater(
		kClass: KClass<*>,
		factory: () -> T,
	) {
		initializerFactory[kClass] = factory
	}

	/**
	 * Use it to create dependency by lazy but in same time will created dependency in scope.
	 * @param kClass T::class of your dependency.
	 * @param factory lambda with your dependency.
	 **/
	internal fun <T : Any> createDependencyNow(
		kClass: KClass<*>,
		factory: () -> T,
	) {
		initializerFactory[kClass] = factory
		listOfDependencies[kClass] = factory.invoke()
	}

	/**
	 * Use it if you need to delete dependency.
	 * @param kClass T::class of your dependency.
	 **/
	internal fun delete(
		kClass: KClass<*>,
	) {
		initializerFactory.remove(kClass)

		listOfDependencies
			.filter { dependency -> dependency == kClass }
			.forEach {
				listOfDependencies.remove(it)
			}
	}

	/**
	 * Use it to check opportunity create the dependency.
	 * @param kClass T::class of your dependency.
	 **/
	internal fun isDependencyInScope(kClass: KClass<*>): Boolean {
		return initializerFactory.containsKey(kClass) || listOfDependencies.any { it == kClass }
	}
}