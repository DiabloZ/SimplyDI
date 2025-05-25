package su.vi.kdi.core.lazy

/**
 * Wrapper for lazy initialize any class.
 */
public class KDILazyWrapper<T : Any>(
	private val lazyValue: () -> T,
) {

	public val value: T by lazy { lazyValue.invoke() }

	public operator fun invoke(): T {
		return value
	}

}