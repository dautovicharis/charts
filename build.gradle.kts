plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.build.config) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ktlint) apply false
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension>("ktlint") {
        android.set(true)
        ignoreFailures.set(false)
    }
}

tasks.register("chartsTest") {
    group = "Charts"
    description = "Relevant tests for the charts project"
    dependsOn("charts:jvmTest")
    dependsOn(":androidApp:validateDebugScreenshotTest")
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
    dependsOn("build")
    dependsOn("chartsTest")

    tasks.findByName("build")?.mustRunAfter(getTasksByName("ktlintCheck", true))
    tasks.findByName("chartsTest")?.mustRunAfter("build")
}

tasks.register("generateJsDemo") {
    group = "Charts"
    description = "Builds the JS app and copies files to docs/static/demo/snapshot (and docs/static/demo/<version> for stable releases)"

    dependsOn(getTasksByName("jsBrowserDistribution", true))
    doLast {
        val isSnapshotVersion = Config.chartsVersion.endsWith("-SNAPSHOT")
        val buildDir = file("app/build/dist/js/productionExecutable")
        val snapshotDestinationDir = file("docs/static/demo/snapshot")

        sync {
            from(buildDir)
            into(snapshotDestinationDir)
        }

        if (!isSnapshotVersion) {
            val versionDestinationDir = file("docs/static/demo/${Config.chartsVersion}")
            sync {
                from(buildDir)
                into(versionDestinationDir)
            }
            println("✅JS Demo generated successfully! Updated snapshot and ${Config.chartsVersion}.")
        } else {
            println("✅JS Demo generated successfully! Updated snapshot.")
        }
    }
}

tasks.register("generateDocs") {
    group = "Charts"
    description = "Generate Dokka API docs and JS demo to docs/static/"

    dependsOn("charts:dokkaGenerate")
    dependsOn("charts:syncDokkaSnapshot")
    dependsOn("generateJsDemo")
    doLast {
        val isSnapshotVersion = Config.chartsVersion.endsWith("-SNAPSHOT")
        if (isSnapshotVersion) {
            println("✅Documentation generated successfully to docs/static/ (updated snapshot)")
        } else {
            println("✅Documentation generated successfully to docs/static/ (updated snapshot and ${Config.chartsVersion})")
        }
    }
}
