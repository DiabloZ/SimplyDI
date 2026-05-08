package su.vi.simply.di.core.utils

import su.vi.simply.di.core.SimplyDILoggerEmpty
import su.vi.simply.di.core.SimplyDILoggerFull

/**
 * Levels of log DI [FULL] or [EMPTY]
 */
public enum class SimplyLogLevel {
	FULL,
	EMPTY
}

internal fun SimplyLogLevel.toSimplyDILogger() = when(this){
	SimplyLogLevel.FULL -> SimplyDILoggerFull()
	SimplyLogLevel.EMPTY -> SimplyDILoggerEmpty()
}