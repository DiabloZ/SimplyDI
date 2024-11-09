package su.vi.simply.di.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Dependency(
	val scopeName: String = "",
	val scopeNames: Array<String> = [],
	val isCreateOnStart: Boolean = false,
	val isSearchInScope: Boolean = false,
	val chainWith: Array<String> = []
)