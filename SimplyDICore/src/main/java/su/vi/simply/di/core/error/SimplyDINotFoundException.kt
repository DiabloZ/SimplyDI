package su.vi.simply.di.core.error

/**
 * An exception with will be thrown if you forget to initialize a dependency.
 */
public class SimplyDINotFoundException(message: String): Throwable(message = message)
