package su.vi.simplydiapp.for_test

import androidx.lifecycle.ViewModel
import su.vi.simply.di.annotations.Dependency
import su.vi.simplydiapp.for_test.package_test.TestInterface321

@Dependency(
	scopeName = "TestScope",
	isCreateOnStart = true
)
class Bububu: ViewModel(), TestInterface321 {

}
