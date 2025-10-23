package su.vi.kdi.core

import su.vi.kdi.Inject
import su.vi.kdi.KDIKey
import su.vi.kdi.Lifecycle
import su.vi.kdi.Named
import su.vi.kdi.Provider
import su.vi.kdi.SingletonProvider
import su.vi.kdi.core.entry_point.KDIContainerDSL
import su.vi.kdi.core.error.KDINotFoundException
import su.vi.kdi.core.utils.KDIConstants.NOT_FOUND_ERROR
import java.lang.reflect.Constructor
import kotlin.collections.filterIsInstance
import kotlin.reflect.KClass

/**
 * DI Scope with lazy initialization of dependencies and fabric methods.
 * @param isSearchInScope if you want to use this container like data store or you need
 * to share dependencies from this container you would set value like true or you can bind them [KDIContainer.addChainScopes].
 */
internal class KDIScope(
	internal val isSearchInScope: Boolean,
	internal val dslBuilder: KDIContainerDSL?,
) {

	internal var isInitialized: Boolean = false

	private val providers: MutableMap<KDIKey, Provider<*>> = mutableMapOf<KDIKey, Provider<*>>()
	private val resolving: ThreadLocal<MutableSet<KDIKey>> = object : ThreadLocal<MutableSet<KDIKey>>() {
		override fun initialValue(): MutableSet<KDIKey> = linkedSetOf()
	}

	/**
	 * Use it to get dependencies with store it in list of dependencies.
	 * If the dependency not created you receive null.
	 * @param clazz T::class of your dependency.
	 * @return T?
	 **/
	@Suppress("UNCHECKED_CAST")
	internal fun <T : Any> getNullableDependency(clazz: Class<T>, name: String? = null): T? {
		val key = KDIKey(clazz, name)
		return providers[key]?.get() as? T
	}

	/**
	 * Use it to get dependencies with store it in list of dependencies.
	 * @param kClass T::class of your dependency.
	 * @return T
	 * @throws KDINotFoundException if the dependency not created you receive.
	 **/
	@Throws(KDINotFoundException::class)
	internal fun <T : Any> getDependency(clazz: Class<T>, name: String? = null): T {
		return getNullableDependency<T>(clazz = clazz)
			?: throw KDINotFoundException(message = String.format(NOT_FOUND_ERROR, clazz.name, name))
	}

	/**
	 * Use it to create dependency by lazy.
	 **/
	internal fun <T : Any> createDependency(
		type: Class<*>,
		name: String? = null,
		lifecycle: Lifecycle = Lifecycle.SINGLETON,
		factory: () -> Any,
	) {
		val provider = when (lifecycle) {
			Lifecycle.SINGLETON -> SingletonProvider(factory)
			Lifecycle.FACTORY -> object : Provider<Any> {
				override fun get(): Any = factory()
			}
		}

		val supertypes = getAllSupertypes(type)
		for (supertype in supertypes) {
			val key = KDIKey(supertype, name)
			providers[key] = provider
		}
	}

	@Suppress("UNCHECKED_CAST")
	internal fun <T : Any> createDependencyLambdaAuto(
		clazz: Class<T>,
		name: String? = null,
		lifecycle: Lifecycle = Lifecycle.SINGLETON,
	) {
		val constructor = selectConstructor(clazz)
		val paramTypes = constructor.parameterTypes
		val annotations = constructor.parameterAnnotations

		createDependency<T>(
			type = clazz,
			name = name,
			lifecycle = lifecycle,
			factory = {
				val key = KDIKey(clazz, name)

				val currentResolving = resolving.get()
				if (!currentResolving.add(key)) {
					val dependencyPath = mutableListOf<String>()
					currentResolving.forEach { dependencyPath.add(it.type.name) }
					val cyclePath = (dependencyPath + clazz.name).joinToString(" -> ")
					error("Cyclic dependency detected involving ${clazz.name}. Path: $cyclePath")
				}

				val args = paramTypes.indices.map { i ->
					val paramName = annotations[i].filterIsInstance<Named>().firstOrNull()?.value
					getDependency(paramTypes[i], paramName)
				}

				resolving.get().remove(key)
				constructor.newInstance(*args.toTypedArray()) as T
			}
		)
	}

	@Suppress("UNCHECKED_CAST")
	internal fun <T : Any> createDependencyAuto(clazz: Class<T>, name: String? = null) {
		val constructor = selectConstructor(clazz)
		val paramTypes = constructor.parameterTypes
		val annotations = constructor.parameterAnnotations

		val provider = SingletonProvider {
			val key = KDIKey(clazz, name)

			val currentResolving = resolving.get()
			if (!currentResolving.add(key)) {
				val dependencyPath = mutableListOf<String>()
				currentResolving.forEach { dependencyPath.add(it.type.name) }
				val cyclePath = (dependencyPath + clazz.name).joinToString(" -> ")
				error("Cyclic dependency detected involving ${clazz.name}. Path: $cyclePath")
			}

			val args = paramTypes.indices.map { i ->
				val paramName = annotations[i].filterIsInstance<Named>().firstOrNull()?.value
				getDependency(paramTypes[i], paramName)
			}

			resolving.get().remove(key)
			constructor.newInstance(*args.toTypedArray()) as? T
		}

		val supertypes = getAllSupertypes(clazz)
		for (supertype in supertypes) {
			val key = KDIKey(supertype, name)
			providers[key] = provider
		}
	}

	@Suppress("UNCHECKED_CAST")
	internal fun <T : Any> createDependencyManually(clazz: Class<T>, name: String? = null, supertypes: List<Class<*>>) {
		val constructor = selectConstructor(clazz)
		val paramTypes = constructor.parameterTypes
		val annotations = constructor.parameterAnnotations

		val provider = SingletonProvider {
			val key = KDIKey(clazz, name)

			val currentResolving = resolving.get()
			if (!currentResolving.add(key)) {
				val dependencyPath = mutableListOf<String>()
				currentResolving.forEach { dependencyPath.add(it.type.name) }
				val cyclePath = (dependencyPath + clazz.name).joinToString(" -> ")
				error("Cyclic dependency detected involving ${clazz.name}. Path: $cyclePath")
			}

			val args = paramTypes.indices.map { i ->
				val paramName = annotations[i].filterIsInstance<Named>().firstOrNull()?.value
				getDependency(paramTypes[i], paramName)
			}

			resolving.get().remove(key)
			constructor.newInstance(*args.toTypedArray()) as? T
		}

		for (supertype in supertypes) {
			val key = KDIKey(supertype, name)
			providers[key] = provider
		}
	}

	private fun getAllSupertypes(type: Class<*>): Set<Class<*>> {
		val result = mutableSetOf<Class<*>>()
		val queue = ArrayDeque<Class<*>>()
		queue += type
		while (queue.isNotEmpty()) {
			val current = queue.removeFirst()
			if (result.add(current)) {
				current.interfaces.forEach(queue::add)
				current.superclass?.let(queue::add)
			}
		}
		return result
	}

	private fun selectConstructor(clazz: Class<*>): Constructor<*> {
		val all = clazz.declaredConstructors
		val inject = all.firstOrNull { it.isAnnotationPresent(Inject::class.java) }
		return inject ?: if (all.size == 1) {
			all[0]
		} else {
			error("Multiple constructors in ${clazz.name} and none marked with @Inject")
		}
	}

	/**
	 * Use it to check opportunity create the dependency.
	 * @param kClass T::class of your dependency.
	 **/
	internal fun isDependencyInScope(kClass: KClass<*>, name: String? = null): Boolean {
		val key = KDIKey(kClass.java, name = name)
		return providers.any { it.key == key }
	}
}
