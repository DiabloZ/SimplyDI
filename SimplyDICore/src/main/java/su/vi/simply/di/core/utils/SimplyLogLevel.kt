package su.vi.simply.di.core.utils

import su.vi.simply.di.core.LogCallback
import su.vi.simply.di.core.LogLevel

public enum class SimplyLogLevel {
    FULL,
    EMPTY
}

internal fun SimplyLogLevel.toLogCallback(): LogCallback? = when (this) {
    SimplyLogLevel.FULL -> { level, tag, msg -> println("[$level/$tag] $msg") }
    SimplyLogLevel.EMPTY -> null
}
