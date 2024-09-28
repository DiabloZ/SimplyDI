package su.vi.simply.di.android

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.view_model.resolveViewModel

@Throws(SimplyDINotFoundException::class)
@MainThread
public inline fun <reified T : ViewModel> ViewModelStoreOwner.simplyAndroidViewModel(
	key: String? = null,
	scopeName: String = DEFAULT_SCOPE_NAME,
): Lazy<T> {
	return lazy(LazyThreadSafetyMode.NONE) {
		resolveViewModel(
			clazz = T::class,
			viewModelStore = this.viewModelStore,
			scopeName = scopeName,
			key = key
		)
	}
}
