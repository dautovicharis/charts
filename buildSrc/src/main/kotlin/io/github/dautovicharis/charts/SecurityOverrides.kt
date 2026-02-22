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
    const val nettyCodecArtifact = "netty-codec"
    const val nettyHttpArtifact = "netty-codec-http"
    const val nettyHttp2Reason = "Mitigate MadeYouReset HTTP/2 DDoS in io.netty:netty-codec-http2 <= 4.1.123.Final"
    const val nettyCodecReason = "Mitigate zip bomb DoS in io.netty:netty-codec < 4.1.125.Final"
    const val nettyHttpReason = "Mitigate CRLF injection in io.netty:netty-codec-http < 4.1.129.Final"

    const val commonsLangGroup = "org.apache.commons"
    const val commonsLang3Artifact = "commons-lang3"
    const val commonsLang3Reason = "Mitigate uncontrolled recursion in org.apache.commons:commons-lang3 < 3.18.0"

    const val logbackGroup = "ch.qos.logback"
    val logbackArtifacts = setOf("logback-core", "logback-classic")
    const val logbackReason = "Mitigate ACE in ch.qos.logback:logback-core < 1.3.16"

    const val httpComponentsGroup = "org.apache.httpcomponents"
    const val httpClientArtifact = "httpclient"
    const val httpClientReason = "Mitigate host confusion in org.apache.httpcomponents:httpclient < 4.5.13"

    const val guavaGroup = "com.google.guava"
    const val guavaArtifact = "guava"
    const val guavaReason = "Mitigate insecure temp directory handling in com.google.guava:guava < 32.0.0-android"

    const val jose4jGroup = "org.bitbucket.b_c"
    const val jose4jArtifact = "jose4j"
    const val jose4jReason = "Mitigate DoS via compressed JWE content in org.bitbucket.b_c:jose4j < 0.9.6"
}
