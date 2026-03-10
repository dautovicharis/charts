import org.gradle.api.Project
import java.io.File

fun Project.patchKarmaMinimatchCompatibility() {
    val karmaLibDirectory =
        layout.buildDirectory
            .dir("js/node_modules/karma/lib")
            .get()
            .asFile
    if (!karmaLibDirectory.exists()) {
        logger.lifecycle(
            "Karma lib directory not found at ${karmaLibDirectory.absolutePath}; " +
                "skipping minimatch compatibility patch.",
        )
        return
    }

    val filesToPatch =
        listOf(
            "file-list.js",
            "helper.js",
            "init.js",
            "preprocessor.js",
            "watcher.js",
        )
    val originalImport = "const mm = require('minimatch')"
    val patchedImport =
        """
        const minimatchPackage = require('minimatch')
        const mm = typeof minimatchPackage === 'function' ? minimatchPackage : minimatchPackage.minimatch
        if (typeof mm !== 'function') {
          throw new Error('Unable to resolve minimatch matcher function from minimatch package')
        }
        """.trimIndent()

    filesToPatch.forEach { fileName ->
        val file = File(karmaLibDirectory, fileName)
        if (!file.exists()) {
            logger.lifecycle("Karma ${file.name} not found at ${file.absolutePath}; skipping.")
            return@forEach
        }

        val source = file.readText()
        when {
            source.contains(originalImport) -> {
                file.writeText(source.replace(originalImport, patchedImport))
                logger.lifecycle("Applied Karma minimatch compatibility patch at ${file.absolutePath}.")
            }

            source.contains("const minimatchPackage = require('minimatch')") -> {
                logger.lifecycle("Karma minimatch compatibility patch already applied at ${file.absolutePath}.")
            }

            else ->
                logger.lifecycle(
                    "No minimatch import pattern found in ${file.absolutePath}; skipping compatibility patch.",
                )
        }
    }
}
