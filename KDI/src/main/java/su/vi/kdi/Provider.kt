package su.vi.kdi

internal interface Provider<T> {
	fun get(): T
}

internal class SingletonProvider<T>(factory: () -> T) : Provider<T> {
	private val instance: T by lazy(factory)
	override fun get(): T = instance
}