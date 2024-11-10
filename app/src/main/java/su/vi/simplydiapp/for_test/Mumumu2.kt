package su.vi.simplydiapp.for_test

import androidx.lifecycle.ViewModel
import su.vi.simply.di.annotations.Dependency
import su.vi.simplydiapp.for_test.package_test.TestInterface111
import su.vi.simplydiapp.for_test.package_test.TestInterface123
import su.vi.simplydiapp.for_test.package_test.TestInterface222
import su.vi.simplydiapp.for_test.package_test.TestInterface321
import su.vi.simplydiapp.for_test.package_test.TestInterface333

@Dependency(
	scopeName = "TestScope",
	scopeNames = ["123", "OtherScopeName"],
	isCreateOnStart = true
)
internal class Mumumu2(
	simplyName: String,
	bububu: TestInterface321?,
	tatata: Tatata,
): ViewModel(), TestInterface123, TestInterface111, TestInterface321, TestInterface333, TestInterface222 {

}