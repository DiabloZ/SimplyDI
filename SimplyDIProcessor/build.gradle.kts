plugins {
/*
	alias(libs.plugins.kotlinMultiplatform)
*/
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrains.kotlin.android)
	id("com.google.devtools.ksp") version "1.7.20-1.0.8"
}

android {
	namespace = libs.versions.nameSpaceSDK.get()
	compileSdk = libs.versions.compileSDK.get().toInt()

	defaultConfig {
		minSdk = libs.versions.minSDKVersionSDK.get().toInt()

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		consumerProguardFiles("consumer-rules.pro")
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
		freeCompilerArgs += "-Xexplicit-api=strict"
	}

}
kotlin {
	sourceSets.main {
		kotlin.srcDir("build/generated/ksp/main/kotlin")
	}
}
dependencies {
	implementation(project(":SimplyDIAnnotations"))
	implementation("com.google.devtools.ksp:symbol-processing-api:1.7.20-1.0.8")
}

/*
plugins {
	kotlin("jvm")
}

dependencies {
	implementation(kotlin("stdlib"))
	implementation("com.google.devtools.ksp:symbol-processing-api:1.7.20-1.0.8")
	implementation(project(":SimplyDIAnnotations"))
}*/

/*plugins {
	//kotlin("multiplatform")
	id("java-library")
}*/

/*kotlin {
*//*	jvm {
		val main by compilations.getting {
			kotlinOptions {
				jvmTarget = "1.8"
			}
		}
	}
	jvm()*//*

	sourceSets {
		val jvmMain by getting {
			dependencies {
				implementation(kotlin("stdlib"))
				//implementation("io.github.anioutkazharkova:di-multiplatform-lib:1.0.4.5")
				implementation(project(":di-multiplatform-core"))
				implementation("com.google.devtools.ksp:symbol-processing-api:123")
				implementation("io.github.diabloz:simply-di-core:1.0.1")

				//code generation
				val kotlinpoetVersion = "1.8.0"
				implementation("com.squareup:kotlinpoet:$kotlinpoetVersion")
				implementation("com.squareup:kotlinpoet-metadata:$kotlinpoetVersion")
				implementation("com.squareup:kotlinpoet-metadata-specs:$kotlinpoetVersion")
				implementation("com.squareup:kotlinpoet-classinspector-elements:$kotlinpoetVersion")

			}
		}
		*//*commonMain {
			dependencies {
				api (kotlin("stdlib-common"))
				api (project(":SimplyDIAnnotations"))
			}
		}

		commonTest {
			dependencies {
			}
		}

		jvmMain {
			dependencies {
				api (kotlin("stdlib-jdk8"))
				api ("com.google.devtools.ksp:symbol-processing-api:1.7.20-1.0.8")
			}
		}

		jvmTest {
			dependencies {
			}
		}*//*

	}
}*/
