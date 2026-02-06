import org.gradle.api.tasks.Delete

tasks.register<Delete>("clean") {
    group = "build"
    description = "Deletes the site, docs/api, and docs/jsdemo directories"

    delete("public-site")
    delete("src/api")
    delete("src/jsdemo")
}
