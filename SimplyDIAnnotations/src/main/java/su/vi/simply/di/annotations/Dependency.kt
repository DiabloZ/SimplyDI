package su.vi.simply.di.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Dependency(
	val scopeName: String,
	val isCreateOnStart: Boolean = false,
)