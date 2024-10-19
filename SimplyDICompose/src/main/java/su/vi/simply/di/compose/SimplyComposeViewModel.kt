package su.vi.simply.di.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.view_model.resolveViewModel

/**
 * Factory where will to get dependency from scope or throw [SimplyDINotFoundException]
 * @param scopeName name of the new container.
 * @param key of dependency in ViewModelProvider.
 * @param viewModelStoreOwner store of viewModels from [LocalViewModelStoreOwner]
 * @throws SimplyDINotFoundException if dependency not found
 */
@Throws(SimplyDINotFoundException::class)
@Composable
public inline fun <reified T : ViewModel> simplyComposeViewModel(
	scopeName: String = DEFAULT_SCOPE_NAME,
	key: String? = null,
	viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
		"No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
	},
): T {
	val viewModel: T by remember(key) {
		mutableStateOf(
			resolveViewModel(
				clazz = T::class,
				viewModelStore = viewModelStoreOwner.viewModelStore,
				key = key,
				scopeName = scopeName,
			)
		)
	}
	return viewModel
}
