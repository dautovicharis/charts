plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.build.config) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.axion.release)
}

val versionCatalog =
    extensions
        .getByType(VersionCatalogsExtension::class.java)
        .named("libs")

scmVersion {
    tag {
        prefix.set("")
    }
    versionIncrementer("incrementMinor")
}

version = scmVersion.version
providers.gradleProperty("chartsReleaseVersion").orNull?.takeIf { it.isNotBlank() }?.let {
    version = it
}

buildscript {
    val buildscriptVersionCatalog =
        project.extensions
            .getByType(VersionCatalogsExtension::class.java)
            .named("libs")

    // Force patched vulnerable transitives on the Gradle plugin classpath (AGP/UTP transitives).
    configurations.configureBuildscriptSecurityOverrides(buildscriptVersionCatalog)
}

// Keep Kotlin/JS transitive dependencies patched in kotlin-js-store/yarn.lock.
configureJsSecurityOverrides(versionCatalog)

// Root project only needs ktlint/logback override; commons-lang3/guava are enforced in subprojects.
configurations.configureProjectSecurityOverrides(
    versionCatalog = versionCatalog,
)

subprojects {
    version = rootProject.version

    tasks.matching { it.name == "jsBrowserTest" }.configureEach {
        doFirst {
            rootProject.patchKarmaMinimatchCompatibility()
        }
    }

    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension>("ktlint") {
        android.set(true)
        ignoreFailures.set(false)
    }

    configurations.configureProjectSecurityOverrides(
        versionCatalog = versionCatalog,
        includeCommonsAndGuava = true,
    )
}

val chartsLibraryModules =
    listOf(
        ":charts-core",
        ":charts-line",
        ":charts-pie",
        ":charts-bar",
        ":charts-stacked-bar",
        ":charts-stacked-area",
        ":charts-radar",
        ":charts",
    )
val chartsPublishableModules = chartsLibraryModules + ":charts-bom"

tasks.register("chartsTest") {
    group = "Charts"
    description = "Relevant tests for the charts project"
    dependsOn("charts:jvmTest")
    dependsOn(":playground:jvmTest")
    dependsOn(":playground:jsTest")
    dependsOn(project(":androidApp").tasks.named("validateDebugScreenshotTest"))
    dependsOn("chartsModulesTest")
}

tasks.register("chartsModulesTest") {
    group = "Charts"
    description = "Runs JVM tests for all modular chart artifacts and the umbrella module"
    dependsOn(chartsLibraryModules.map { "$it:jvmTest" })
    dependsOn("smokeLineCompile")
}

tasks.register("smokeLineCompile") {
    group = "Charts"
    description = "Smoke compile of a module that depends only on charts-line"
    dependsOn(":smoke-line:compileKotlinJvm")
}

tasks.register("updateScreenshots") {
    group = "Charts"
    description = "Updates Android screenshot test baselines (debug variant)"
    dependsOn(":androidApp:updateDebugScreenshotTest")
}

tasks.register("chartsCheck") {
    group = "Charts"
    description = "Build and tests for the charts project"
    dependsOn(getTasksByName("ktlintCheck", true))
    dependsOn("buildSrcKtlintCheck")
    dependsOn("build")
    dependsOn("chartsTest")

    tasks.findByName("build")?.mustRunAfter(getTasksByName("ktlintCheck", true))
    tasks.findByName("chartsTest")?.mustRunAfter("build")
}

tasks.register<Exec>("buildSrcKtlintCheck") {
    group = "verification"
    description = "Runs ktlintCheck for buildSrc Kotlin code and scripts"
    commandLine("./gradlew", "-p", "buildSrc", "ktlintCheck")
}

tasks.register("publishChartsModules") {
    group = "publishing"
    description = "Publishes all charts modules and BOM to the configured Maven repository"
    dependsOn(chartsPublishableModules.map { "$it:publish" })
}

tasks.register("publishChartsModulesToMavenLocal") {
    group = "publishing"
    description = "Publishes all charts modules and BOM to Maven Local"
    dependsOn(chartsPublishableModules.map { "$it:publishToMavenLocal" })
}

tasks.register("generateJsDemo") {
    group = "Charts"
    description = "Builds the JS app and copies files to docs/static/demo/<target-version>"

    // Only the demo app distribution is needed for docs/static/demo.
    // Depending on all jsBrowserDistribution tasks triggers unnecessary production JS builds
    // in every module and can make generateDocs appear to hang.
    dependsOn(":app:jsBrowserDistribution")
    doLast {
        val isSnapshotVersion = project.version.toString().endsWith("-SNAPSHOT")
        val buildDir = file("app/build/dist/js/productionExecutable")

        if (isSnapshotVersion) {
            val snapshotDestinationDir = file("docs/static/demo/snapshot")
            sync {
                from(buildDir)
                into(snapshotDestinationDir)
            }
            println("✅JS Demo generated successfully! Updated snapshot.")
        } else {
            val versionDestinationDir = file("docs/static/demo/${project.version}")
            sync {
                from(buildDir)
                into(versionDestinationDir)
            }
            println("✅JS Demo generated successfully! Updated ${project.version}.")
        }
    }
}

tasks.register("playground") {
    group = "Charts"
    description =
        "Builds the JS playground app (development bundle) and copies files to docs/static/playground/snapshot (snapshot versions only)"

    val isSnapshotVersion = project.version.toString().endsWith("-SNAPSHOT")
    onlyIf { isSnapshotVersion }
    if (isSnapshotVersion) {
        dependsOn(":playground:jsBrowserDevelopmentExecutableDistribution")
    }

    doLast {
        val buildDir = file("playground/build/dist/js/developmentExecutable")
        val snapshotDestinationDir = file("docs/static/playground/snapshot")

        sync {
            from(buildDir)
            into(snapshotDestinationDir)
        }

        println("✅JS Playground (development bundle) generated successfully! Updated snapshot.")
    }
}

tasks.register("generateDocs") {
    group = "Charts"
    description = "Generate Dokka API docs and JS demo to docs/static/"

    dependsOn("charts:dokkaGenerate")
    dependsOn("generateJsDemo")
    doLast {
        val isSnapshotVersion = project.version.toString().endsWith("-SNAPSHOT")
        if (isSnapshotVersion) {
            println("✅Documentation generated successfully to docs/static/ (updated snapshot)")
        } else {
            println("✅Documentation generated successfully to docs/static/ (updated ${project.version})")
        }
    }
}

tasks.register("listDocsGifScenarios") {
    group = "Charts"
    description = "Lists available docs GIF scenarios discovered via @RecordGif in :androidApp"
    dependsOn(":androidApp:listGifScenarios")
}

tasks.register("recordDocsGif") {
    group = "Charts"
    description =
        "Records one docs GIF scenario to docs/content/<gifDocsVersion>/wiki/assets (set -PgifScenario=<name>, defaults to first)"
    dependsOn(":androidApp:recordGifDebug")
}

tasks.register("recordDocsGifs") {
    group = "Charts"
    description =
        "Records all docs GIF scenarios to docs/content/<gifDocsVersion>/wiki/assets (default version: snapshot)"
    dependsOn(":androidApp:recordGifsDebug")
}
