package su.vi.simplydiapp.for_test.mu

import androidx.lifecycle.ViewModel
import su.vi.simply.di.annotations.Dependency
import su.vi.simplydiapp.for_test.Tatata
import su.vi.simplydiapp.for_test.package_test.TestInterface111
import su.vi.simplydiapp.for_test.package_test.TestInterface123
import su.vi.simplydiapp.for_test.package_test.TestInterface222
import su.vi.simplydiapp.for_test.package_test.TestInterface321
import su.vi.simplydiapp.for_test.package_test.TestInterface333

@Dependency(
	//scopeName = "TestScope2",
	scopeNames = ["123", "OtherScopeName"],
	chainWith = ["TestScope"],
	isCreateOnStart = true,
	isFullLogger = true
)
internal class Mumumu2(
	simplyName: String,
	bububu: TestInterface321?,
	tatata: Tatata,
): ViewModel(), TestInterface123, TestInterface111, TestInterface321, TestInterface333, TestInterface222 {

}

@Dependency(
	//scopeName = "TestScope2",
	scopeNames = ["123"],
	chainWith = ["TestScope"],
	isCreateOnStart = true,
	isFullLogger = false
)
internal class Mumumu3(
	simplyName: String,
	bububu: TestInterface321?,
	tatata: Tatata,
): ViewModel(), TestInterface123, TestInterface111, TestInterface321, TestInterface333, TestInterface222 {

}