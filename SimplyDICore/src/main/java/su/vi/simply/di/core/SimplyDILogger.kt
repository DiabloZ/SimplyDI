package su.vi.simply.di.core

import su.vi.simply.di.core.utils.SimplyLogLevel

/**
 * [SimplyLogLevel] where you can set level of logs for you needs for example for release
 * would do use [SimplyLogLevel.EMPTY] for debug [SimplyLogLevel.FULL].
 */
internal abstract class SimplyDILogger {
	internal open fun d(tag: String, text: String, throwable: Throwable? = null) = Unit
	internal open fun e(tag: String, text: String, throwable: Throwable? = null) = Unit
	internal open fun wtf(tag: String, text: String, throwable: Throwable? = null) = Unit
}

/**
 * There you will receive Logs with TAG "SIMPLY DI..."
 */
internal class SimplyDILoggerFull: SimplyDILogger() {
	override fun d(tag: String, text: String, throwable: Throwable?) {
		println("DEBUG TAG - $tag, TEXT - $text, TR - $throwable")
	}
	override fun e(tag: String, text: String, throwable: Throwable?) {
		println("ERROR TAG - $tag, TEXT - $text, TR - $throwable")
	}
	override fun wtf(tag: String, text: String, throwable: Throwable?) {
		println("WTF TAG - $tag, TEXT - $text, TR - $throwable")
	}
}

/**
 * There aren't any messages.
 */
internal class SimplyDILoggerEmpty: SimplyDILogger()
