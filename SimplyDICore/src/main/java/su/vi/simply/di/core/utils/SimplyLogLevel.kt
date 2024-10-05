package su.vi.simply.di.core.utils

import su.vi.simply.di.core.SimplyDILoggerEmpty
import su.vi.simply.di.core.SimplyDILoggerFull

public enum class SimplyLogLevel {
	FULL,
	EMPTY
}

internal fun SimplyLogLevel.toSimplyDILogger() = when(this){
	SimplyLogLevel.FULL -> SimplyDILoggerFull()
	SimplyLogLevel.EMPTY -> SimplyDILoggerEmpty()
}