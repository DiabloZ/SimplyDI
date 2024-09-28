package su.vi.simplydiapp

import android.os.Bundle
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
import su.vi.simply.di.core.SimplyDILogger
import su.vi.simplydiapp.ui.theme.SimplyDITheme

class MainActivity : ComponentActivity() {
	val asdas by simplyAndroidViewModel<Bububu>(key = "1")

	override fun onCreate(savedInstanceState: Bundle?) {
		SimplyDILogger.d("SIMPLY DI CONTAINER", asdas.hashCode().toString())

		SimplyDILogger.d("SIMPLY DI CONTAINER", "!!!!!!!!!!!!!!")
		val a1 = simplyAndroidViewModel<Bububu>()
		SimplyDILogger.d("SIMPLY DI CONTAINER", a1.hashCode().toString())
		val a2 = simplyAndroidViewModel<Bububu>()
		SimplyDILogger.d("SIMPLY DI CONTAINER", a2.hashCode().toString())
		val a3 = simplyAndroidViewModel<Bububu>()
		SimplyDILogger.d("SIMPLY DI CONTAINER", a3.hashCode().toString())
		val a4 = simplyAndroidViewModel<Bububu>()
		SimplyDILogger.d("SIMPLY DI CONTAINER", a4.hashCode().toString())
		val a5 = simplyAndroidViewModel<Bububu>()
		SimplyDILogger.d("SIMPLY DI CONTAINER", a5.hashCode().toString())
		val a6 = simplyAndroidViewModel<Bububu>()
		SimplyDILogger.d("SIMPLY DI CONTAINER", a6.hashCode().toString())
		val a7 = simplyAndroidViewModel<Bububu>()
		SimplyDILogger.d("SIMPLY DI CONTAINER", a7.hashCode().toString())
		val a8 = simplyAndroidViewModel<Bububu>()
		SimplyDILogger.d("SIMPLY DI CONTAINER", a8.hashCode().toString())
		val a9 = simplyAndroidViewModel<Bububu>("")
		SimplyDILogger.d("SIMPLY DI CONTAINER", a9.hashCode().toString())
		val a10 = simplyAndroidViewModel<Bububu>("")
		SimplyDILogger.d("SIMPLY DI CONTAINER", a10.hashCode().toString())
		SimplyDILogger.d("SIMPLY DI CONTAINER", "!!!!!!!!!!!!!!")
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			SimplyDITheme {
				val asdas = simplyComposeViewModel<Bububu>()
				val asdas22 = simplyComposeViewModel<Bububu>()
				val asdas2 = simplyComposeViewModel<Bububu>("1")
				SimplyDILogger.d("SIMPLY DI CONTAINER", asdas.hashCode().toString())
				SimplyDILogger.d("SIMPLY DI CONTAINER", asdas22.hashCode().toString())
				SimplyDILogger.d("SIMPLY DI CONTAINER", asdas2.hashCode().toString())
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					Greeting(
						name = "Android",
						modifier = Modifier.padding(innerPadding)
					)
				}
			}
		}
	}
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
	val asdas = simplyComposeViewModel<Bububu>(key = "4")
	SimplyDILogger.d("SIMPLY DI CONTAINER", asdas.hashCode().toString())
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