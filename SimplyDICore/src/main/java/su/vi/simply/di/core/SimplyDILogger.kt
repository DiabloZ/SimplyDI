package su.vi.simply.di.core

/**
 * Log levels for SimplyDI.
 */
public enum class LogLevel {
    DEBUG, ERROR, WTF
}

/**
 * Lambda type for logging. Pass null for no logging (default).
 */
public typealias LogCallback = (level: LogLevel, tag: String, message: String) -> Unit
