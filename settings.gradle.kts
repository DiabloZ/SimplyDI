pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}


rootProject.name = "SimplyDI"
include(":app")
include(":SimplyDICore")
include(":SimplyDIAndroid")
include(":SimplyDICompose")
include(":SimplyDIViewModel")
project(":SimplyDICore").projectDir = file("SimplyDICore")