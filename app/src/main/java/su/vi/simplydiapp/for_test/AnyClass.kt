package su.vi.simplydiapp.for_test

import androidx.lifecycle.ViewModel
import su.vi.simply.di.core.lazy.SimplyDILazyWrapper

class AnyClass : ViewModel() {

}

class TestLazyClass(
	val anyClass: AnyClass,
	val anyClassInWrapper: SimplyDILazyWrapper<AnyClass>,
)