plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.jetbrains.kotlin.android)
}

android {
	namespace = libs.versions.nameSpaceApp.get()
	compileSdk = libs.versions.compileSDK.get().toInt()

	defaultConfig {
		applicationId = libs.versions.nameSpaceApp.get()
		minSdk = libs.versions.minSDKVersionApp.get().toInt()
		targetSdk = libs.versions.compileSDK.get().toInt()
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = JavaVersion.VERSION_1_8.toString()
	}
	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtensionVersion.get()
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

kotlin {
	explicitApi = org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Strict
}

dependencies {
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)

	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)

	implementation(project(":SimplyDICore"))
/*	implementation(project(":SimplyDIAndroid"))
	implementation(project(":SimplyDIViewModel"))
	implementation(project(":SimplyDICompose"))
	implementation("io.github.diabloz:simply-di-core:1.0.2")*/
	implementation("io.github.diabloz:simply-di-android:1.0.2")
	implementation("io.github.diabloz:simply-di-viewmodel:1.0.2")
	implementation("io.github.diabloz:simply-di-compose:1.0.2")
}