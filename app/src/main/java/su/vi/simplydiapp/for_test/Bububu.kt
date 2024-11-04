package su.vi.simplydiapp.for_test

import androidx.lifecycle.ViewModel
import su.vi.simply.di.annotations.Dependency

@Dependency(
	scopeName = "TestScope",
	isCreateOnStart = true
)
class Bububu: ViewModel(){

}