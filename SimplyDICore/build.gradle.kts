import com.vanniktech.maven.publish.SonatypeHost

plugins {
	alias(libs.plugins.kotlin.jvm)
	//alias(libs.plugins.vanniktechMavenPublish)
}

repositories {
	mavenCentral()
}

kotlin {
	explicitApi = org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Strict
}

dependencies {
/*	testImplementation(libs.junit)
	testImplementation(libs.androidx.junit)
	testImplementation(libs.androidx.espresso.core)*/
}

/*mavenPublishing {
	coordinates(
		groupId = "io.github.diabloz",
		artifactId = "simply-di-core", //
		version = libs.versions.commonVersion.get()
	)
	pom {
		name = "SimplyDICore" //
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
}*/

/*configurePublishing(
	artifactId = "simply-di-core",
	projectName = "SimplyDICore",
	projectDescription = "The simplest and lightest library for DI",
	projectVersion = "1.0.1"
)*/
