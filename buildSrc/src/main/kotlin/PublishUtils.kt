
/*import com.vanniktech.maven.publish.MavenPublishPlugin
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Project

fun Project.configurePublishing(
	artifactId: String = "simply-di-android",
	projectName: String = "",
	projectDescription: String = "The simplest and lightest library for DI",
	projectVersion: String = "1.0.1",
) {

	plugins.apply(MavenPublishPlugin::class.java)

	extensions.configure(MavenPublishBaseExtension::class.java) {
		coordinates(
			groupId = "io.github.diabloz",
			artifactId = artifactId,
			version = projectVersion
		)
		pom {
			name.set(projectName)
			description.set(projectDescription)
			url.set("https://github.com/DiabloZ/SimplyDI")
			inceptionYear.set("2024")

			licenses {
				license {
					name.set("The Apache License, Version 2.0")
					url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
				}
			}
			developers {
				developer {
					id.set("DiabloZ")
					name.set("Vitaly Suhov")
					email.set("DiabloZ@me.com")
				}
			}
			scm {
				connection.set("scm:git:git://github.com/DiabloZ/SimplyDI.git")
				developerConnection.set("scm:git:ssh://github.com:DiabloZ/SimplyDI.git")
				url.set("https://github.com/DiabloZ/SimplyDI")
			}
			publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
			signAllPublications()
		}
	}
}*/
