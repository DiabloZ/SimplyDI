package su.vi.simply.di.core

import su.vi.simply.di.core.error.SimplyDINotFoundException
import kotlin.reflect.KClass

/**
 * DI с возможностью отложенной инициализации,
 * пока генерирует только сингл-тоны.
 * также есть возможность сделать фабрики.
 *
 * @author Сухов Виталий
 */
internal class SimplyDIScope(
	val scopeName: String,
	val isSearchInScope: Boolean
) {

	private val initializerFactory: MutableMap<Any, () -> Any> = mutableMapOf()

	private val listOfDependencies: MutableSet<Any> = mutableSetOf()

	/**
	 * Метод для получения нулабельной зависимости
	 * @return T
	 * @throws SimplyDINotFoundException если зависимость не будет найдена
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
	 * Метод для получения зависимости
	 * @return T
	 * @throws SimplyDINotFoundException если зависимость не будет найдена
	 **/
	@Suppress("UNCHECKED_CAST")
	@Throws(SimplyDINotFoundException::class)
	internal fun <T: Any> getDependency(clazz: KClass<*>): T {
		val dependency = listOfDependencies.find { dependency -> dependency::class == clazz } ?: run {
			val newInstance = initializerFactory[clazz]?.invoke()
				?: throw SimplyDINotFoundException(message = String.format(NOT_FOUND_ERROR, clazz.simpleName))
			listOfDependencies.add(newInstance)
			newInstance
		}
		return (dependency as? T)
			?: throw SimplyDINotFoundException(message = String.format(NOT_FOUND_ERROR, clazz.simpleName))
	}

	@Suppress("UNCHECKED_CAST")
	internal fun <T: Any> getFactoryDependency(clazz: KClass<*>): T {
		return initializerFactory[clazz]?.invoke() as? T
			?: throw SimplyDINotFoundException(message = String.format(NOT_FOUND_ERROR, clazz.simpleName))
	}

	/**
	 * Метод для запроса зависимости по clazz с нуллабельностью.
	 * @return T?
	 **/
	@Suppress("UNCHECKED_CAST")
	internal fun <T: Any> getByClass(
		clazz: KClass<*>,
	): T? {
 		return initializerFactory[clazz]?.invoke() as? T
	}

	/**
	 * Метод для запроса зависимости по clazz с ошибкой, если он не будет найден.
	 * @return T
	 * @throws SimplyDINotFoundException если зависимость не будет найдена
	 **/
	@Suppress("UNCHECKED_CAST")
	internal fun <T> getByClassAnyway(
		clazz: KClass<*>,
	): T {
		return initializerFactory[clazz]?.invoke() as? T
			?: throw SimplyDINotFoundException(message = String.format(NOT_FOUND_ERROR, clazz.simpleName))
	}

	/**
	 * Метод для добавления инициализатора в фабрику (для отложенной инициализации)
	 **/
	internal fun <T: Any> createDependencyLater(
		clazz: KClass<*>,
		factory: () -> T
	) {
		initializerFactory[clazz] = factory
	}

	/**
	 * Метод для добавления зависимости сразу в set (К примеру контекст должен бысть сразу
	 * зарегистрирован как зависимость, так как от него зависит много сервисов)
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
	 * Метод удаления инициализатора из фабрики и зависимости из set-а
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

	internal fun inDependencyInScope(clazz: KClass<*>) = initializerFactory.containsKey(clazz) || listOfDependencies.any { it == clazz }

	companion object {
		private const val NOT_FOUND_ERROR = "In the beginning, you need to register such a service - %s, before calling it"
	}

}