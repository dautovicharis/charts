object ChartsModules {
    val library =
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

    val publishable = library + ":charts-bom"
    val ciKmpCompile = library + listOf(":app", ":playground")
    val ciAndroidCompile = library + listOf(":app")
}
