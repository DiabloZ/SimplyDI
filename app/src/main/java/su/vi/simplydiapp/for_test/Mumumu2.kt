package su.vi.simplydiapp.for_test

import androidx.lifecycle.ViewModel
import su.vi.simply.di.annotations.Dependency

@Dependency(
	scopeName = "TestScope",
	scopeNames = ["123", "OtherScopeName"],
	isCreateOnStart = true
)
class Mumumu2(
	simplyName: String,
	bububu: Bububu?,
	tatata: Tatata,
): ViewModel(), TestInterface123, TestInterface111, TestInterface321, TestInterface333, TestInterface222 {

}