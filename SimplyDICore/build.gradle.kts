import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `java-library`
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.vanniktechMavenPublish)
}

kotlin {
    explicitApi()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.test {
    useJUnit()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
}


mavenPublishing {
    coordinates(
        groupId = "io.github.diabloz",
        artifactId = "simply-di-core",
        version = libs.versions.coreSimplyVersion.get()
    )
    pom {
        name = "SimplyDICore"
        description = "The simplest and lightest library for DI"
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
