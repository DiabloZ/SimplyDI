pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
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