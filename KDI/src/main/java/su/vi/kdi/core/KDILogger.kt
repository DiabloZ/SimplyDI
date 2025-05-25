package su.vi.kdi.core

import su.vi.kdi.core.utils.KDILogLevel

/**
 * [KDILogLevel] where you can set level of logs for you needs for example for release
 * would do use [KDILogLevel.Empty] for debug [KDILogLevel.Full].
 */
public interface KDILogger {
	public fun d(tag: String, text: String, throwable: Throwable? = null): Unit
	public fun e(tag: String, text: String, throwable: Throwable? = null): Unit
	public fun wtf(tag: String, text: String, throwable: Throwable? = null): Unit
}



/**
 * There aren't any messages.
 */
internal class KDILoggerEmpty: KDILogger {
	override fun d(tag: String, text: String, throwable: Throwable?) {}

	override fun e(tag: String, text: String, throwable: Throwable?) {}

	override fun wtf(tag: String, text: String, throwable: Throwable?) {}
}
