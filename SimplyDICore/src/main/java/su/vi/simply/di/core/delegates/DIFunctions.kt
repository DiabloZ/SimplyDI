package su.vi.simply.di.core.delegates

import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.core.entry_point.getDependency

/**
 * Delegate to get dependency by lazy from [SimplyDIContainer]. You can call it from any place.
 * @param scopeName name of your scope with you declared before.
 * @param mode lazy thread mode [LazyThreadSafetyMode], recommended use option by default.
 * @param T instance of class.
 *
 * @throws SimplyDINotFoundException if dependency not initialized before with [scopeName]
 */
@Throws(SimplyDINotFoundException::class)
public inline fun <reified T : Any> inject(
	scopeName: String = DEFAULT_SCOPE_NAME,
	mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
): Lazy<T> = lazy(mode) {
	SimplyDIContainer.instance.getDependency<T>(
		scopeName = scopeName,
		clazz = T::class
	)
}

@Throws(SimplyDINotFoundException::class)
public inline fun <reified T : Any> inject(
	container: SimplyDIContainer = SimplyDIContainer.instance,
	mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
): Lazy<T> = lazy(mode) {
	container.getDependency<T>(
		scopeName = container.scopeName,
		clazz = T::class
	)
}