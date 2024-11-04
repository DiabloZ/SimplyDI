plugins {
	alias(libs.plugins.android.application) apply false
	alias(libs.plugins.jetbrains.kotlin.android) apply false
	alias(libs.plugins.android.library) apply false
	alias(libs.plugins.vanniktechMavenPublish) apply false
	alias(libs.plugins.jetbrains.kotlin.jvm) apply false
	alias(libs.plugins.kotlinMultiplatform).apply(false)
	id("com.google.devtools.ksp") version "1.7.20-1.0.8" apply false

}

allprojects {
	tasks.withType<JavaCompile> {
		sourceCompatibility = JavaVersion.VERSION_1_8.toString()
		targetCompatibility = JavaVersion.VERSION_1_8.toString()
	}
}

subprojects {
	tasks.withType<JavaCompile> {
		sourceCompatibility = JavaVersion.VERSION_1_8.toString()
		targetCompatibility = JavaVersion.VERSION_1_8.toString()
	}
}
