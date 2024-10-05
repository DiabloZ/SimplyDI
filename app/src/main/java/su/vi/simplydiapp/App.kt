package su.vi.simplydiapp

import android.app.Application
import androidx.lifecycle.ViewModel
import su.vi.simply.di.android.initializeAndroid
import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.utils.SimplyDIConstants.DEFAULT_SCOPE_NAME
import su.vi.simply.di.core.utils.addChainScopes
import su.vi.simply.di.core.utils.addDependencyLater
import su.vi.simply.di.core.utils.addDependencyNow
import su.vi.simply.di.core.utils.initialize
import su.vi.simplydiapp.for_test.Bububu

class App: Application() {
	override fun onCreate() {
		super.onCreate()
		SimplyDIContainer.instance.initializeAndroid(context = this)
		SimplyDIContainer.instance.initialize()
		SimplyDIContainer.instance.initialize(scopeName = "6", isSearchInScope = false)
		SimplyDIContainer.instance.initialize(scopeName = "6", isSearchInScope = false)
		SimplyDIContainer.instance.initialize(scopeName = "5", isSearchInScope = true)
		SimplyDIContainer.instance.initialize(scopeName = "4", isSearchInScope = false)
		SimplyDIContainer.instance.initialize(scopeName = "3", isSearchInScope = true)
		SimplyDIContainer.instance.initialize(scopeName = "2", isSearchInScope = true)
		SimplyDIContainer.instance.initialize(scopeName = "1", isSearchInScope = true)
		SimplyDIContainer.instance.initialize(scopeName = "12345", isSearchInScope = true)
		SimplyDIContainer.instance.addDependencyNow<Bububu>() {
			Bububu()
		}
		SimplyDIContainer.instance.addDependencyNow<Bububu>(scopeName = "1") {
			Bububu()
		}
		SimplyDIContainer.instance.addDependencyLater<Bububu>(scopeName = "12345") {
			Bububu()
		}
		SimplyDIContainer.instance.addChainScopes(
			listOfScopes = listOf("6", "12345", DEFAULT_SCOPE_NAME, "5")
		)
		//SimplyDIContainer.instance.depBenchmark<Bububu>()
	}
}