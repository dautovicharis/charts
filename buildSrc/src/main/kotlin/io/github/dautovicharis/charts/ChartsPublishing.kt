import org.gradle.api.publish.maven.MavenPom

object ChartsPublishing {
    fun configurePom(
        pom: MavenPom,
        moduleName: String,
        moduleDescription: String,
    ) {
        pom.name.set(moduleName)
        pom.description.set(moduleDescription)
        pom.inceptionYear.set(Config.inceptionYear)
        pom.url.set(Config.projectUrl)

        pom.licenses {
            license {
                name.set(Config.licenseName)
                url.set(Config.licenseUrl)
            }
        }
        pom.developers {
            developer {
                id.set(Config.developerId)
                name.set(Config.developerName)
                email.set(Config.developerEmail)
            }
        }
        pom.issueManagement {
            system.set(Config.issueSystem)
            url.set(Config.issueUrl)
        }
        pom.scm {
            connection.set(Config.scmConnection)
            url.set(Config.projectUrl)
        }
    }
}
