import com.vanniktech.maven.publish.SonatypeHost

plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrains.kotlin.android)
	alias(libs.plugins.vanniktechMavenPublish)
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

dependencies {
/*	implementation(project(":SimplyDICore"))
	implementation(project(":SimplyDIViewModel"))*/
	implementation("io.github.diabloz:simply-di-core:1.0.1")
	implementation("io.github.diabloz:simply-di-viewmodel:1.0.1")

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.material)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
}

mavenPublishing {
	coordinates(
		groupId = "io.github.diabloz",
		artifactId = "simply-di-android", //
		version = "1.0.1"
	)
	pom {
		name = "SimplyDIAndroid" //
		description = "The simplest and lightest library for DI" //
		url = "https://github.com/DiabloZ/SimplyDI"
		inceptionYear = "2024"

		licenses {
			license {
				name = "The Apache License, Version 2.0"
				url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
			}
		}

		developers {
			developer {
				id = "DiabloZ"
				name = "Vitaly Suhov"
				email = "DiabloZ@me.com"
			}
		}

		scm {
			connection = "scm:git:git://github.com/DiabloZ/SimplyDI.git"
			developerConnection = "scm:git:ssh://github.com:DiabloZ/SimplyDI.git"
			url = "https://github.com/DiabloZ/SimplyDI"
		}

		publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

		signAllPublications()
	}
}