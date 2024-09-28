package su.vi.simply.di.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import su.vi.simply.di.core.error.SimplyDINotFoundException
import kotlin.reflect.KClass

@Throws(SimplyDINotFoundException::class)
public fun <T : ViewModel> resolveViewModel(
	scopeName: String,
	key: String? = null,
	clazz: KClass<T>,
	viewModelStore: ViewModelStore,
): T {
	val modelClass: Class<T> = clazz.java
	val factory = SimplyViewModelFactory(
		clazz = clazz,
		scopeName = scopeName
	)
	val provider = ViewModelProvider(
		store = viewModelStore,
		factory = factory,
		defaultCreationExtras = CreationExtras.Empty
	)
	return when {
		key != null -> provider[key, modelClass]
		else -> provider[modelClass]
	}
}