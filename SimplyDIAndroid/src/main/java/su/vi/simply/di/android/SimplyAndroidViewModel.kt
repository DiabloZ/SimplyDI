package su.vi.simply.di.android

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.view_model.resolveViewModel

/**
 * Factory where will to get dependency from scope or throw [SimplyDINotFoundException]
 * @param scopeName name of the new container.
 * @param key of dependency in ViewModelProvider.
 * @throws SimplyDINotFoundException if dependency not found
 */
@Throws(SimplyDINotFoundException::class)
@MainThread
public inline fun <reified T : ViewModel> ViewModelStoreOwner.simplyAndroidViewModel(
	scopeName: String = DEFAULT_SCOPE_NAME,
	key: String? = null,
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
