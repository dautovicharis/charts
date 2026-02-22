buildscript {
    val versionCatalog =
        project.extensions
            .getByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java)
            .named("libs")

    val protobufSecurityVersion =
        versionCatalog
            .findVersion("protobuf-security")
            .get()
            .requiredVersion
    val jdomSecurityVersion =
        versionCatalog
            .findVersion("jdom-security")
            .get()
            .requiredVersion
    val nettySecurityVersion =
        versionCatalog
            .findVersion("netty-codec-http2-security")
            .get()
            .requiredVersion
    val commonsLang3SecurityVersion =
        versionCatalog
            .findVersion("commons-lang3-security")
            .get()
            .requiredVersion
    val httpClientSecurityVersion =
        versionCatalog
            .findVersion("httpclient-security")
            .get()
            .requiredVersion
    val guavaSecurityVersion =
        versionCatalog
            .findVersion("guava-security")
            .get()
            .requiredVersion
    val jose4jSecurityVersion =
        versionCatalog
            .findVersion("jose4j-security")
            .get()
            .requiredVersion

    // Force patched vulnerable transitives on the Gradle plugin classpath (AGP/UTP transitives).
    configurations.configureEach {
        if (name == "classpath") {
            resolutionStrategy.eachDependency {
                if (requested.group == SecurityOverrides.protobufGroup &&
                    requested.name in SecurityOverrides.protobufArtifacts
                ) {
                    useVersion(protobufSecurityVersion)
                    because(SecurityOverrides.protobufReason)
                }
                if (requested.group == SecurityOverrides.jdomGroup &&
                    requested.name == SecurityOverrides.jdomArtifact
                ) {
                    useVersion(jdomSecurityVersion)
                    because(SecurityOverrides.jdomReason)
                }
                if (requested.group == SecurityOverrides.nettyGroup &&
                    requested.name == SecurityOverrides.nettyHttp2Artifact
                ) {
                    useVersion(nettySecurityVersion)
                    because(SecurityOverrides.nettyHttp2Reason)
                }
                if (requested.group == SecurityOverrides.nettyGroup &&
                    requested.name == SecurityOverrides.nettyCodecArtifact
                ) {
                    useVersion(nettySecurityVersion)
                    because(SecurityOverrides.nettyCodecReason)
                }
                if (requested.group == SecurityOverrides.nettyGroup &&
                    requested.name == SecurityOverrides.nettyHttpArtifact
                ) {
                    useVersion(nettySecurityVersion)
                    because(SecurityOverrides.nettyHttpReason)
                }
                if (requested.group == SecurityOverrides.commonsLangGroup &&
                    requested.name == SecurityOverrides.commonsLang3Artifact
                ) {
                    useVersion(commonsLang3SecurityVersion)
                    because(SecurityOverrides.commonsLang3Reason)
                }
                if (requested.group == SecurityOverrides.httpComponentsGroup &&
                    requested.name == SecurityOverrides.httpClientArtifact
                ) {
                    useVersion(httpClientSecurityVersion)
                    because(SecurityOverrides.httpClientReason)
                }
                if (requested.group == SecurityOverrides.guavaGroup &&
                    requested.name == SecurityOverrides.guavaArtifact
                ) {
                    useVersion(guavaSecurityVersion)
                    because(SecurityOverrides.guavaReason)
                }
                if (requested.group == SecurityOverrides.jose4jGroup &&
                    requested.name == SecurityOverrides.jose4jArtifact
                ) {
                    useVersion(jose4jSecurityVersion)
                    because(SecurityOverrides.jose4jReason)
                }
            }
        }
    }
}

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
    alias(libs.plugins.ktlint) apply false
}

val logbackSecurityVersion = libs.versions.logback.core.security.get()
val ajvSecurityVersion = libs.versions.ajv.security.get()
val minimatchSecurityVersion = libs.versions.minimatch.security.get()

// Keep Kotlin/JS transitive ajv patched in kotlin-js-store/yarn.lock.
org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension[project]
    .resolution("ajv", ajvSecurityVersion)
org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension[project]
    .resolution("minimatch", minimatchSecurityVersion)

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension>("ktlint") {
        android.set(true)
        ignoreFailures.set(false)
    }

    configurations.configureEach {
        if (name == "ktlint") {
            resolutionStrategy.eachDependency {
                if (requested.group == SecurityOverrides.logbackGroup &&
                    requested.name in SecurityOverrides.logbackArtifacts
                ) {
                    useVersion(logbackSecurityVersion)
                    because(SecurityOverrides.logbackReason)
                }
            }
        }
    }
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
    dependsOn("build")
    dependsOn("chartsTest")

    tasks.findByName("build")?.mustRunAfter(getTasksByName("ktlintCheck", true))
    tasks.findByName("chartsTest")?.mustRunAfter("build")
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
        val isSnapshotVersion = Config.chartsVersion.endsWith("-SNAPSHOT")
        val buildDir = file("app/build/dist/js/productionExecutable")

        if (isSnapshotVersion) {
            val snapshotDestinationDir = file("docs/static/demo/snapshot")
            sync {
                from(buildDir)
                into(snapshotDestinationDir)
            }
            println("✅JS Demo generated successfully! Updated snapshot.")
        } else {
            val versionDestinationDir = file("docs/static/demo/${Config.chartsVersion}")
            sync {
                from(buildDir)
                into(versionDestinationDir)
            }
            println("✅JS Demo generated successfully! Updated ${Config.chartsVersion}.")
        }
    }
}

tasks.register("playground") {
    group = "Charts"
    description = "Builds the JS playground app (development bundle) and copies files to docs/static/playground/snapshot (snapshot versions only)"

    val isSnapshotVersion = Config.chartsVersion.endsWith("-SNAPSHOT")
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
        val isSnapshotVersion = Config.chartsVersion.endsWith("-SNAPSHOT")
        if (isSnapshotVersion) {
            println("✅Documentation generated successfully to docs/static/ (updated snapshot)")
        } else {
            println("✅Documentation generated successfully to docs/static/ (updated ${Config.chartsVersion})")
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
    description = "Records all docs GIF scenarios to docs/content/<gifDocsVersion>/wiki/assets (default version: snapshot)"
    dependsOn(":androidApp:recordGifsDebug")
}
