package su.vi.simply.di.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.error.SimplyDINotFoundException
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.core.entry_point.getByClassAnyway
import kotlin.reflect.KClass

/**
 * Factory where will to get dependency from scope or throw [SimplyDINotFoundException]
 * @param clazz Class extended ViewModel.
 * @param scopeName name of the new container.
 * @throws SimplyDINotFoundException
 */
internal class SimplyViewModelFactory(
	private val clazz: KClass<out ViewModel>,
	private val scopeName: String = DEFAULT_SCOPE_NAME,
) : ViewModelProvider.Factory {

	@Throws(SimplyDINotFoundException::class)
	override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
		return SimplyDIContainer.instance.getByClassAnyway(clazz = clazz, scopeName = scopeName)
	}
}
