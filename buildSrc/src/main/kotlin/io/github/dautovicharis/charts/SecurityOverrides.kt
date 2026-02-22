object SecurityOverrides {
    const val protobufGroup = "com.google.protobuf"
    val protobufArtifacts =
        setOf(
            "protobuf-java",
            "protobuf-java-util",
            "protobuf-javalite",
            "protobuf-kotlin",
            "protobuf-kotlin-lite",
        )
    const val protobufReason = "Mitigate CVE-2024-7254 / GHSA-735f-pc8j-v9w8"

    const val jdomGroup = "org.jdom"
    const val jdomArtifact = "jdom2"
    const val jdomReason = "Mitigate XXE in org.jdom:jdom2 < 2.0.6.1"
}
