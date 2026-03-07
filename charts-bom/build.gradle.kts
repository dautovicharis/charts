plugins {
    `java-platform`
    `maven-publish`
    signing
    alias(libs.plugins.mavenPublish)
}

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api("${Config.groupId}:${Config.artifactCoreId}:${project.version}")
        api("${Config.groupId}:${Config.artifactLineId}:${project.version}")
        api("${Config.groupId}:${Config.artifactPieId}:${project.version}")
        api("${Config.groupId}:${Config.artifactBarId}:${project.version}")
        api("${Config.groupId}:${Config.artifactStackedBarId}:${project.version}")
        api("${Config.groupId}:${Config.artifactStackedAreaId}:${project.version}")
        api("${Config.groupId}:${Config.artifactRadarId}:${project.version}")
        api("${Config.groupId}:${Config.artifactId}:${project.version}")
    }
}

mavenPublishing {
    coordinates(
        groupId = Config.groupId,
        artifactId = Config.artifactBomId,
        version = project.version.toString(),
    )

    pom {
        ChartsPublishing.configurePom(
            pom = this,
            moduleName = "Charts BOM",
            moduleDescription = "Bill of Materials (BOM) for aligning Charts module versions.",
        )
    }
}
