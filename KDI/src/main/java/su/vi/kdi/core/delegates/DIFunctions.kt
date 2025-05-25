package su.vi.kdi.core.delegates

import su.vi.kdi.core.KDIContainer
import su.vi.kdi.core.error.KDINotFoundException
import su.vi.kdi.core.utils.KDIConstants.DEFAULT_SCOPE_NAME
import su.vi.kdi.core.entry_point.getDependency

/**
 * Delegate to get dependency by lazy from [KDIContainer]. You can call it from any place.
 * @param scopeName name of your scope with you declared before.
 * @param mode lazy thread mode [LazyThreadSafetyMode], recommended use option by default.
 * @param T instance of class.
 *
 * @throws KDINotFoundException if dependency not initialized before with [scopeName]
 */
@Throws(KDINotFoundException::class)
public inline fun <reified T : Any> inject(
	scopeName: String = DEFAULT_SCOPE_NAME,
	mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
): Lazy<T> = lazy(mode) {
	KDIContainer.instance.getDependency<T>(
		scopeName = scopeName,
		clazz = T::class
	)
}

@Throws(KDINotFoundException::class)
public inline fun <reified T : Any> inject(
	container: KDIContainer = KDIContainer.instance,
	mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
): Lazy<T> = lazy(mode) {
	container.getDependency<T>(
		scopeName = container.scopeName,
		clazz = T::class
	)
}