package su.vi.simply.di.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import su.vi.simply.di.view_model.resolveViewModel

@Composable
public inline fun <reified T : ViewModel> simplyComposeViewModel(
	key: String? = null,
	qualifier: String? = null,
	viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
		"No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
	},
	scopeName: String = "Main",
): T {
	val viewModel: T by remember(key, qualifier) {
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
