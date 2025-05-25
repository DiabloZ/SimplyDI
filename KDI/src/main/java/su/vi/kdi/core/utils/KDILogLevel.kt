package su.vi.kdi.core.utils

import su.vi.kdi.core.KDILogger
import su.vi.kdi.core.KDILoggerEmpty

/**
 * Levels of log DI [FULL] or [EMPTY]
 */
public sealed class KDILogLevel {
	/**
	 * [KDILogLevel.Full] - will log all messages.
	 */
	public class Full(public val logger: KDILogger) : KDILogLevel()

	/**
	 * [KDILogLevel.Empty] - will not log any messages.
	 */
	public object Empty : KDILogLevel()
}

internal fun KDILogLevel.toKDILogger() = when(this){
	is KDILogLevel.Full -> logger
	is KDILogLevel.Empty -> KDILoggerEmpty()
}