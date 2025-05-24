package su.vi.kdi

import java.lang.reflect.Constructor

public class BitSetDIContainer {

	private val providers = mutableMapOf<Key, Provider<*>>()
	private val resolving = object : ThreadLocal<MutableSet<Key>>() {
		override fun initialValue(): MutableSet<Key> = mutableSetOf()
	}

	public inline fun <reified T : Any> register(
		name: String? = null,
		lifecycle: Lifecycle = Lifecycle.SINGLETON,
		noinline providerFn: () -> T,
	) {
		register(type = T::class.java, name = name, lifecycle = lifecycle, providerFn = providerFn)
	}

	public fun register(
		type: Class<*>,
		name: String? = null,
		lifecycle: Lifecycle = Lifecycle.SINGLETON,
		providerFn: () -> Any,
	) {
		val provider = when (lifecycle) {
			Lifecycle.SINGLETON -> SingletonProvider(providerFn)
			Lifecycle.FACTORY -> object : Provider<Any> {
				override fun get(): Any = providerFn()
			}
		}

		val supertypes = getAllSupertypes(type)
		for (supertype in supertypes) {
			val key = Key(supertype, name)
			providers[key] = provider
		}
	}

	public inline fun <reified T : Any> registerAuto(name: String? = null) {
		registerAuto(T::class.java, name)
	}

	@Suppress("UNCHECKED_CAST")
	public fun <T : Any> registerAuto(clazz: Class<T>, name: String? = null) {
		val constructor = selectConstructor(clazz)
		val paramTypes = constructor.parameterTypes
		val annotations = constructor.parameterAnnotations

		val provider = SingletonProvider {
			val key = Key(clazz, name)
			if (!resolving.get().add(key)) {
				error("Cyclic dependency detected involving ${clazz.name}")
			}

			val args = paramTypes.indices.map { i ->
				val paramName = annotations[i].filterIsInstance<Named>().firstOrNull()?.value
				resolve(paramTypes[i], paramName)
			}

			resolving.get().remove(key)
			constructor.newInstance(*args.toTypedArray()) as? T
		}

		val supertypes = getAllSupertypes(clazz)
		for (supertype in supertypes) {
			val key = Key(supertype, name)
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

	public inline fun <reified T : Any> resolve(name: String? = null) :T = resolve(T::class.java, name)

	@Suppress("UNCHECKED_CAST")
	public fun <T : Any> resolve(clazz: Class<T>, name: String? = null): T {
		val key = Key(clazz, name)
		return providers[key]?.get() as? T
			?: error("No provider registered for ${clazz.name} name=${name ?: "null"}")
	}

	private fun selectConstructor(clazz: Class<*>): Constructor<*> {
		val all = clazz.declaredConstructors
		val inject = all.firstOrNull { it.isAnnotationPresent(Inject::class.java) }
		return inject ?: if (all.size == 1) all[0]
		else error("Multiple constructors in ${clazz.name} and none marked with @Inject")
	}
}