plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.build.config) apply false
    alias(libs.plugins.sonarqube) apply true
    alias(libs.plugins.kover) apply true
    alias(libs.plugins.compose.compiler) apply false
}

sonar {
    properties {
        property("sonar.projectKey", "dautovicharis_Charts")
        property("sonar.organization", "dautovicharis")
        property("sonar.host.url", "https://sonarcloud.io/")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${project.rootDir}/charts/build/reports/kover/report.xml," //+
                    //"${project.rootDir}/app/build/reports/kover/report.xml"
        )
        property("sonar.coverage.exclusions", "**/preview/**")
        property("sonar.exclusions", "**/app/**")
    }
}

tasks.register("chartsTest") {
    group = "Charts"
    description = "Relevant tests for the charts project"
    dependsOn("charts:iosX64Test")
    dependsOn("charts:connectedAndroidTest")
    dependsOn("charts:jsTest")
    dependsOn("charts:jvmTest")

    tasks.findByName("charts:connectedAndroidTest")?.mustRunAfter("charts:iosX64Test")
    tasks.findByName("charts:jsTest")?.mustRunAfter("charts:connectedAndroidTest")
    tasks.findByName("charts:jvmTest")?.mustRunAfter("charts:jsTest")
}

tasks.register("chartsCheck") {
    group = "Charts"
    description = "Build, tests, coverage report and sonar analysis for the charts project"
    dependsOn("build")
    dependsOn("chartsTest")
    dependsOn("charts:koverXmlReport")
    dependsOn("sonar")

    tasks.findByName("chartsTest")?.mustRunAfter("build")
    tasks.findByName("charts:koverXmlReport")?.mustRunAfter("chartsTest")
    tasks.findByName("sonar")?.mustRunAfter("charts:koverXmlReport")
}

tasks.register("generateJsDemo") {
    group = "Charts"
    description = "Builds the JS app and copies necessary files to docs/src/jsdemo"

    dependsOn(getTasksByName("jsBrowserDistribution", true))
    doLast {
        val buildDir = file("app/build/dist/js/productionExecutable")
        val destinationDir = file("docs/src/jsdemo")

        if (!destinationDir.exists()) {
            destinationDir.mkdirs()
        }

        copy {
            from(buildDir)
            into(destinationDir)
        }

        println("✅JS Demo generated successfully!")
    }
}

tasks.register("generateDocs") {
    group = "Charts"
    description = "Generate the documentation with version index page"

    dependsOn("charts:dokkaGenerate")
    dependsOn("generateJsDemo")
    println("✅Documentation generated successfully!")
}
