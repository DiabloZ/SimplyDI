package su.vi.simply.di.core

import android.util.Log

internal abstract class SimplyDILogger {
	internal open fun d(tag: String, text: String, throwable: Throwable? = null) = Unit
	internal open fun e(tag: String, text: String, throwable: Throwable? = null) = Unit
	internal open fun wtf(tag: String, text: String, throwable: Throwable? = null) = Unit
}

internal class SimplyDILoggerFull: SimplyDILogger() {
	override fun d(tag: String, text: String, throwable: Throwable?) {
		Log.d(tag, text, throwable)
	}
	override fun e(tag: String, text: String, throwable: Throwable?) {
		Log.e(tag, text, throwable)
	}
	override fun wtf(tag: String, text: String, throwable: Throwable?) {
		Log.wtf(tag, text, throwable)
	}
}

internal class SimplyDILoggerEmpty: SimplyDILogger()
