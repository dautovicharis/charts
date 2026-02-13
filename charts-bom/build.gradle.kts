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
        api("${Config.groupId}:${Config.artifactCoreId}:${Config.chartsVersion}")
        api("${Config.groupId}:${Config.artifactLineId}:${Config.chartsVersion}")
        api("${Config.groupId}:${Config.artifactPieId}:${Config.chartsVersion}")
        api("${Config.groupId}:${Config.artifactBarId}:${Config.chartsVersion}")
        api("${Config.groupId}:${Config.artifactStackedBarId}:${Config.chartsVersion}")
        api("${Config.groupId}:${Config.artifactStackedAreaId}:${Config.chartsVersion}")
        api("${Config.groupId}:${Config.artifactRadarId}:${Config.chartsVersion}")
        api("${Config.groupId}:${Config.artifactId}:${Config.chartsVersion}")
    }
}

mavenPublishing {
    coordinates(
        groupId = Config.groupId,
        artifactId = Config.artifactBomId,
        version = Config.chartsVersion,
    )

    pom {
        ChartsPublishing.configurePom(
            pom = this,
            moduleName = "Charts BOM",
            moduleDescription = "Bill of Materials (BOM) for aligning Charts module versions.",
        )
    }
}
