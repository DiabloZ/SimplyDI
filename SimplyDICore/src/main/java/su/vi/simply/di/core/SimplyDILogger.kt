package su.vi.simply.di.core

import android.util.Log

public object SimplyDILogger {
	public fun d(tag: String, text: String, throwable: Throwable? = null) {
		Log.d(tag, text, throwable)
	}
	public fun e(tag: String, text: String, throwable: Throwable? = null) {
		Log.e(tag, text, throwable)
	}
}
