package su.vi.kdi.core.error

/**
 * An exception with will be thrown if you forget to initialize a dependency.
 */
public class KDINotFoundException(message: String): Throwable(message = message)
