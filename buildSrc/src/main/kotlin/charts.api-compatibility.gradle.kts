import me.champeau.gradle.japicmp.JapicmpTask
import org.gradle.api.GradleException
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.register

plugins {
    id("me.champeau.gradle.japicmp")
}

val apiCompatibilityBaselineProperty = "apiCompatibilityBaselineVersion"
// japicmp --exclude expects wildcard expressions, not regex.
val apiCompatibilityInternalExcludePattern = "*.internal.*"

fun Project.requireApiCompatibilityBaselineVersion(): String =
    providers.gradleProperty(apiCompatibilityBaselineProperty).orNull
        ?: providers.gradleProperty("baselineVersion").orNull
        ?: throw GradleException(
            "Missing baseline version. Run with -P$apiCompatibilityBaselineProperty=<released-version>.",
        )

fun String.toArtifactId(): String = removePrefix(":")

fun String.toTaskSuffix(): String =
    split('-', '.')
        .filter { it.isNotBlank() }
        .joinToString("") { token ->
            token.replaceFirstChar { firstChar -> firstChar.uppercase() }
        }

val apiCompatibilityTasks =
    ChartsModules.library.map { projectPath ->
        val artifactId = projectPath.toArtifactId()
        tasks.register<JapicmpTask>("apiCompatibility${artifactId.toTaskSuffix()}") {
            group = "verification"
            description = "Checks binary/source API compatibility for $artifactId against a released baseline."

            dependsOn("$projectPath:jvmJar")
            accessModifier = "public"
            ignoreMissingClasses = true
            onlyModified = true
            failOnModification = true
            onlyBinaryIncompatibleModified = true
            failOnSourceIncompatibility = true
            packageExcludes = listOf(apiCompatibilityInternalExcludePattern)
            mdOutputFile.set(layout.buildDirectory.file("reports/api-compatibility/$artifactId.md"))

            val baselineVersionProvider =
                providers.provider {
                    project.requireApiCompatibilityBaselineVersion()
                }
            val oldJarProvider =
                baselineVersionProvider.map { baselineVersion ->
                    configurations
                        .detachedConfiguration(
                            dependencies.create("${Config.GROUP_ID}:$artifactId-jvm:$baselineVersion"),
                        ).apply {
                            isTransitive = false
                        }.singleFile
                }
            val newJarProvider =
                project(projectPath)
                    .tasks
                    .named<Jar>("jvmJar")
                    .flatMap { it.archiveFile }
                    .map { it.asFile }

            oldArchives.from(oldJarProvider)
            newArchives.from(newJarProvider)
            oldClasspath.from(oldJarProvider)
            newClasspath.from(newJarProvider)

            doFirst {
                logger.lifecycle(
                    "Running API compatibility for $artifactId-jvm against baseline ${baselineVersionProvider.get()}",
                )
            }
        }
    }

tasks.register("apiCompatibilityCheck") {
    group = "verification"
    description =
        "Checks published JVM artifacts for breaking API changes vs a baseline release (-P$apiCompatibilityBaselineProperty=x.y.z)."
    dependsOn(apiCompatibilityTasks)
}
