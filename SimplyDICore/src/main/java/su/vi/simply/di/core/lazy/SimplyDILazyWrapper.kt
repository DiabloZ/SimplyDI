package su.vi.simply.di.core.lazy

/**
 * Wrapper for lazy initialize any class.
 */
public class SimplyDILazyWrapper<T : Any>(
	private val lazyValue: () -> T,
) {

	public val value: T by lazy { lazyValue.invoke() }

	public operator fun invoke(): T {
		return value
	}

}