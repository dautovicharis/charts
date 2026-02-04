
object Config {
    // Common
    const val compileSdk = 36
    const val minSdk = 24
    const val targetSdk = 36
    const val groupId = "io.github.dautovicharis"
    const val artifactId = "charts"

    // Demo app
    const val demoNamespace = "$groupId.$artifactId.app"
    const val demoLibraryNamespace = "$groupId.$artifactId.app.library"
    const val demoVersionName = "1.0.0"
    const val demoVersionCode = 4

    // Charts library
    const val chartsVersion = "2.0.2-SNAPSHOT"
    const val chartsNamespace = "$groupId.$artifactId"
}
