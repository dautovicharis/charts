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
}
