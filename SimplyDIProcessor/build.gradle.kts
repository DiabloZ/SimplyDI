plugins {
	alias(libs.plugins.kotlin.jvm)
}

repositories {
	mavenCentral()
}

kotlin {
	explicitApi = org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Strict
}

dependencies {
	implementation(project(":SimplyDIAnnotations"))
	implementation("com.google.devtools.ksp:symbol-processing-api:1.7.20-1.0.8")
}
