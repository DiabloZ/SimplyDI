package su.vi.simply.di.core

import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.utils.SimplyDIConstants.NOT_FOUND_ERROR
import su.vi.simply.di.core.utils.addChainScopes
import kotlin.reflect.KClass

/**
 * DI Scope with lazy initialization of dependencies and fabric methods.
 * @param isSearchInScope if you want to use this container like data store or you need
 * to share dependencies from this container you would set value like true or you can bind them [SimplyDIContainer.addChainScopes].
 */
internal class SimplyDIScope(
	val isSearchInScope: Boolean
) {

	private val initializerFactory: MutableMap<Any, () -> Any> = mutableMapOf()

	private val listOfDependencies: MutableSet<Any> = mutableSetOf()

	/**
	 * Use it to get dependencies with store it in list of dependencies.
	 * If the dependency not created you receive null.
	 * @param clazz T::class of your dependency.
	 * @return T?
	 **/
	@Suppress("UNCHECKED_CAST")
	internal fun <T: Any> getNullableDependency(clazz: KClass<*>): T? {
		val dependency = listOfDependencies.find { dependency -> dependency::class == clazz } ?: run {
			val newInstance = initializerFactory[clazz]?.invoke()
				?: return null
			listOfDependencies.add(newInstance)
			newInstance
		}
		return (dependency as? T)
	}

	/**
	 * Use it to get dependencies with store it in list of dependencies.
	 * @param clazz T::class of your dependency.
	 * @return T
	 * @throws SimplyDINotFoundException if the dependency not created you receive.
	 **/
	@Throws(SimplyDINotFoundException::class)
	internal fun <T: Any> getDependency(clazz: KClass<*>): T {
		return getNullableDependency<T>(clazz = clazz)
			?: throw SimplyDINotFoundException(message = String.format(NOT_FOUND_ERROR, clazz.simpleName))
	}

	/**
	 * Use it to get dependencies without store it in list of dependencies.
	 * @param clazz T::class of your dependency.
	 * @return T
	 * @throws SimplyDINotFoundException if the dependency not created you receive.
	 **/
	@Suppress("UNCHECKED_CAST")
	internal fun <T: Any> getFactoryDependency(clazz: KClass<*>): T {
		return initializerFactory[clazz]?.invoke() as? T
			?: throw SimplyDINotFoundException(message = String.format(NOT_FOUND_ERROR, clazz.simpleName))
	}

	/**
	 * Use it to get dependencies without store it in list of dependencies.
	 * If the dependency not created you receive null.
	 * @param clazz T::class of your dependency.
	 * @return T?
	 **/
	@Suppress("UNCHECKED_CAST")
	internal fun <T: Any> getByClass(
		clazz: KClass<*>,
	): T? {
 		return initializerFactory[clazz]?.invoke() as? T
	}

	/**
	 * Use it to get dependencies without store it in list of dependencies.
	 * @param clazz T::class of your dependency.
	 * @return T
	 * @throws SimplyDINotFoundException if the dependency not created you receive.
	 **/
	@Suppress("UNCHECKED_CAST")
	internal fun <T> getByClassAnyway(
		clazz: KClass<*>,
	): T {
		return initializerFactory[clazz]?.invoke() as? T
			?: throw SimplyDINotFoundException(message = String.format(NOT_FOUND_ERROR, clazz.simpleName))
	}

	/**
	 * Use it to create dependency by lazy.
	 **/
	internal fun <T: Any> createDependencyLater(
		clazz: KClass<*>,
		factory: () -> T
	) {
		initializerFactory[clazz] = factory
	}

	/**
	 * Use it to create dependency by lazy but in same time will created dependency in scope.
	 * @param clazz T::class of your dependency.
	 * @param factory lambda with your dependency.
	 **/
	internal fun <T: Any> createDependencyNow(
		clazz: KClass<*>,
		factory: () -> T
	) {
		initializerFactory[clazz] = factory
		listOfDependencies.add(
			factory.invoke()
		)
	}

	/**
	 * Use it if you need to delete dependency.
	 * @param clazz T::class of your dependency.
	 **/
	internal fun delete(
		clazz: KClass<*>,
	) {
		initializerFactory.remove(clazz)

		listOfDependencies
			.filter { dependency -> dependency == clazz }
			.forEach {
				listOfDependencies.remove(it)
			}
	}

	/**
	 * Use it to check opportunity create the dependency.
	 * @param clazz T::class of your dependency.
	 **/
	internal fun isDependencyInScope(clazz: KClass<*>): Boolean {
		return initializerFactory.containsKey(clazz) || listOfDependencies.any { it == clazz }
	}

}