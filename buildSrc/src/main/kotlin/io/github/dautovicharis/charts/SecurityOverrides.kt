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

    const val nettyGroup = "io.netty"
    const val nettyHttp2Artifact = "netty-codec-http2"
    const val nettyHttp2Reason = "Mitigate MadeYouReset HTTP/2 DDoS in io.netty:netty-codec-http2 <= 4.1.123.Final"

    const val jose4jGroup = "org.bitbucket.b_c"
    const val jose4jArtifact = "jose4j"
    const val jose4jReason = "Mitigate DoS via compressed JWE content in org.bitbucket.b_c:jose4j < 0.9.6"
}
