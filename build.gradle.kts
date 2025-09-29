plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.build.config) apply false
    alias(libs.plugins.compose.compiler) apply false
}

tasks.register("chartsTest") {
    group = "Charts"
    description = "Relevant tests for the charts project"
    dependsOn("charts:iosX64Test")
    dependsOn("charts:connectedAndroidTest")
    // Temporarily exclude jsTest due to Skia/Compose UI compatibility issues in browser environment
    // dependsOn("charts:jsTest")
    dependsOn("charts:jvmTest")

    tasks.findByName("charts:connectedAndroidTest")?.mustRunAfter("charts:iosX64Test")
    tasks.findByName("charts:jvmTest")?.mustRunAfter("charts:connectedAndroidTest")
}

tasks.register("chartsCheck") {
    group = "Charts"
    description = "Build and tests for the charts project"
    dependsOn("build")
    dependsOn("chartsTest")

    tasks.findByName("chartsTest")?.mustRunAfter("build")
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
