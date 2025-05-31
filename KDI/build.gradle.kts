import com.vanniktech.maven.publish.SonatypeHost

plugins {
	id("java-library")
	alias(libs.plugins.jetbrains.kotlin.jvm)
	alias(libs.plugins.vanniktechMavenPublish)
}
java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}
kotlin {
	compilerOptions {
		jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8
	}
	explicitApi = org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Strict
}

mavenPublishing {
	coordinates(
		groupId = "io.github.diabloz",
		artifactId = "kdi-core", //
		version = libs.versions.commonKDIVersion.get()
	)
	pom {
		name = "KDICore" //
		description = "The simplest and lightest library for DI" //
		url = "https://github.com/DiabloZ/SimplyDI"
		inceptionYear = "2025"

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