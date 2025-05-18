package su.vi.simplydiapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import su.vi.simply.di.android.simplyAndroidViewModel
import su.vi.simply.di.compose.simplyComposeViewModel
import su.vi.simply.di.core.SimplyDIContainer
import su.vi.simply.di.core.delegates.inject
import su.vi.simply.di.core.utils.SimplyLogLevel
import su.vi.simply.di.core.entry_point.addDependencyLater
import su.vi.simply.di.core.entry_point.getByClassAnyway
import su.vi.simply.di.core.entry_point.getDependency
import su.vi.simply.di.core.entry_point.getDependencyByLazy
import su.vi.simply.di.core.entry_point.initializeContainer
import su.vi.simplydiapp.for_test.AnyClass
import su.vi.simplydiapp.ui.theme.SimplyDITheme

class MainActivity : ComponentActivity() {
	private val asdas by simplyAndroidViewModel<AnyClass>(key = "123")
	private val asdass by inject<AnyClass>(scopeName = "1")

	override fun onCreate(savedInstanceState: Bundle?) {
		SomePublicClassFromOtherModule.init()
		SomePublicClassFromOtherModule.getOther()
		SimplyDIContainer("testSomeContainer2", false).initializeContainer(
			simplyLogLevel = SimplyLogLevel.FULL,
		)
		val container: SimplyDIContainer = SomePublicClassFromOtherModule.container
		container.addDependencyLater(scopeName = "testSomeContainer2") {
			AnyClass()
		}

		val dependency10 = container.getDependency<AnyClass>(scopeName = "testSomeContainer2", clazz = AnyClass::class)
		val dependency20 =
			container.getByClassAnyway<AnyClass>(scopeName = "testSomeContainer2", clazz = AnyClass::class)
		val dependency30 = container.getDependency<AnyClass>(scopeName = "testSomeContainer2", clazz = AnyClass::class)
		val dependency40 =
			container.getDependencyByLazy<AnyClass>(scopeName = "testSomeContainer2", clazz = AnyClass::class)

		val dependency01 = container.getDependency<AnyClass>(scopeName = "testSomeContainer2", clazz = AnyClass::class)
		val dependency02 =
			container.getByClassAnyway<AnyClass>(scopeName = "testSomeContainer2", clazz = AnyClass::class)
		val dependency03 = container.getDependency<AnyClass>(scopeName = "testSomeContainer2", clazz = AnyClass::class)
		val dependency04 =
			container.getDependencyByLazy<AnyClass>(scopeName = "testSomeContainer2", clazz = AnyClass::class)


		assert(dependency10 == dependency01)
		assert(dependency20 != dependency02)
		assert(dependency30 == dependency03)
		assert(dependency40.value == dependency04.value)

		val someStorage = App.container.getDependency<SomeStorage>()
		val someStorage2 = App.container.getDependency<SomeStorage>()
		val someStorage3 = App.container.getDependency<SomeStorage>()
		val someStorage4 = SomePublicClassFromOtherModule.container.getDependency<SomeStorage>()
		val someStorage5 = SomePublicClassFromOtherModule.container.getDependency<SomeStorage>()
		val someStorage6 = SomePublicClassFromOtherModule.container.getDependency<SomeStorage>()
		println("??????????????" + someStorage)
		println("??????????????" + someStorage2)
		println("??????????????" + someStorage3)
		println("??????????????" + someStorage4)
		println("??????????????" + someStorage5)
		println("??????????????" + someStorage6)



		Log.d("SIMPLY DI CONTAINER", asdas.hashCode().toString())
		Log.d("SIMPLY DI CONTAINER", asdass.hashCode().toString())

		Log.d("SIMPLY DI CONTAINER", "!!!!!!!!!!!!!!")
		val a1 by simplyAndroidViewModel<AnyClass>()
		Log.d("SIMPLY DI CONTAINER", a1.hashCode().toString())
		val a2 by simplyAndroidViewModel<AnyClass>()
		Log.d("SIMPLY DI CONTAINER", a2.hashCode().toString())
		val a3 by simplyAndroidViewModel<AnyClass>(key = "312")
		Log.d("SIMPLY DI CONTAINER", a3.hashCode().toString())
		val a4 by simplyAndroidViewModel<AnyClass>(key = "312")
		Log.d("SIMPLY DI CONTAINER", a4.hashCode().toString())
		val a5 by simplyAndroidViewModel<AnyClass>(key = "312")
		Log.d("SIMPLY DI CONTAINER", a5.hashCode().toString())
		val a6 by simplyAndroidViewModel<AnyClass>(key = "312")
		Log.d("SIMPLY DI CONTAINER", a6.hashCode().toString())
		val a7 by simplyAndroidViewModel<AnyClass>()
		Log.d("SIMPLY DI CONTAINER", a7.hashCode().toString())
		val a8 by simplyAndroidViewModel<AnyClass>()
		Log.d("SIMPLY DI CONTAINER", a8.hashCode().toString())
		val a9 by simplyAndroidViewModel<AnyClass>(key = "312")
		Log.d("SIMPLY DI CONTAINER", a9.hashCode().toString())
		val a10 by simplyAndroidViewModel<AnyClass>("")
		Log.d("SIMPLY DI CONTAINER", a10.hashCode().toString())
		val a11 by simplyAndroidViewModel<AnyClass>(key = "123")
		Log.d("SIMPLY DI CONTAINER", a11.hashCode().toString())
		Log.d("SIMPLY DI CONTAINER", "!!!!!!!!!!!!!!")

		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			SimplyDITheme {
				val asdas5 = simplyComposeViewModel<AnyClass>(scopeName = "5")
				val asdas50 = simplyComposeViewModel<AnyClass>(scopeName = "5", key = "!!!")
				val asdas222 = simplyComposeViewModel<AnyClass>()
				val asdas22 = simplyComposeViewModel<AnyClass>()
				val asdas2 = simplyComposeViewModel<AnyClass>("1")
				Log.d("SIMPLY DI CONTAINER", asdas5.hashCode().toString())
				Log.d("SIMPLY DI CONTAINER", asdas50.hashCode().toString())
				Log.d("SIMPLY DI CONTAINER", asdas222.hashCode().toString())
				Log.d("SIMPLY DI CONTAINER", asdas22.hashCode().toString())
				Log.d("SIMPLY DI CONTAINER", asdas2.hashCode().toString())
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					val asdasss by inject<AnyClass>(scopeName = "1")
					Greeting(
						name = "Android $asdasss",
						modifier = Modifier.padding(innerPadding)
					)
				}
			}
		}
	}
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
	val asdas = simplyComposeViewModel<AnyClass>(key = "4")
	Log.d("SIMPLY DI CONTAINER", asdas.hashCode().toString())
	Text(
		text = "Hello $name!",
		modifier = modifier
	)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
	SimplyDITheme {
		Greeting("Android")
	}
}