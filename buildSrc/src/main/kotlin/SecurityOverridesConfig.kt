import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.VersionCatalog

fun ConfigurationContainer.configureBuildscriptSecurityOverrides(versionCatalog: VersionCatalog) {
    val protobufSecurityVersion = versionCatalog.requiredVersion("protobuf-security")
    val jdomSecurityVersion = versionCatalog.requiredVersion("jdom-security")
    val nettySecurityVersion = versionCatalog.requiredVersion("netty-codec-http2-security")
    val commonsLang3SecurityVersion = versionCatalog.requiredVersion("commons-lang3-security")
    val httpClientSecurityVersion = versionCatalog.requiredVersion("httpclient-security")
    val guavaSecurityVersion = versionCatalog.requiredVersion("guava-security")
    val jose4jSecurityVersion = versionCatalog.requiredVersion("jose4j-security")

    configureEach {
        if (name == "classpath") {
            resolutionStrategy.eachDependency {
                if (requested.group == SecurityOverrides.PROTOBUF_GROUP &&
                    requested.name in SecurityOverrides.PROTOBUF_ARTIFACTS
                ) {
                    useVersion(protobufSecurityVersion)
                    because(SecurityOverrides.PROTOBUF_REASON)
                }
                if (requested.group == SecurityOverrides.JDOM_GROUP &&
                    requested.name == SecurityOverrides.JDOM_ARTIFACT
                ) {
                    useVersion(jdomSecurityVersion)
                    because(SecurityOverrides.JDOM_REASON)
                }
                if (requested.group == SecurityOverrides.NETTY_GROUP &&
                    requested.name == SecurityOverrides.NETTY_HTTP2_ARTIFACT
                ) {
                    useVersion(nettySecurityVersion)
                    because(SecurityOverrides.NETTY_HTTP2_REASON)
                }
                if (requested.group == SecurityOverrides.NETTY_GROUP &&
                    requested.name == SecurityOverrides.NETTY_CODEC_ARTIFACT
                ) {
                    useVersion(nettySecurityVersion)
                    because(SecurityOverrides.NETTY_CODEC_REASON)
                }
                if (requested.group == SecurityOverrides.NETTY_GROUP &&
                    requested.name == SecurityOverrides.NETTY_HTTP_ARTIFACT
                ) {
                    useVersion(nettySecurityVersion)
                    because(SecurityOverrides.NETTY_HTTP_REASON)
                }
                if (requested.group == SecurityOverrides.COMMONS_LANG_GROUP &&
                    requested.name == SecurityOverrides.COMMONS_LANG3_ARTIFACT
                ) {
                    useVersion(commonsLang3SecurityVersion)
                    because(SecurityOverrides.COMMONS_LANG3_REASON)
                }
                if (requested.group == SecurityOverrides.HTTP_COMPONENTS_GROUP &&
                    requested.name == SecurityOverrides.HTTP_CLIENT_ARTIFACT
                ) {
                    useVersion(httpClientSecurityVersion)
                    because(SecurityOverrides.HTTP_CLIENT_REASON)
                }
                if (requested.group == SecurityOverrides.GUAVA_GROUP &&
                    requested.name == SecurityOverrides.GUAVA_ARTIFACT
                ) {
                    useVersion(guavaSecurityVersion)
                    because(SecurityOverrides.GUAVA_REASON)
                }
                if (requested.group == SecurityOverrides.JOSE4J_GROUP &&
                    requested.name == SecurityOverrides.JOSE4J_ARTIFACT
                ) {
                    useVersion(jose4jSecurityVersion)
                    because(SecurityOverrides.JOSE4J_REASON)
                }
            }
        }
    }
}

fun ConfigurationContainer.configureProjectSecurityOverrides(
    versionCatalog: VersionCatalog,
    includeCommonsAndGuava: Boolean = false,
) {
    val commonsLang3SecurityVersion = versionCatalog.requiredVersion("commons-lang3-security")
    val guavaSecurityVersion = versionCatalog.requiredVersion("guava-security")
    val logbackSecurityVersion = versionCatalog.requiredVersion("logback-core-security")

    configureEach {
        if (includeCommonsAndGuava) {
            resolutionStrategy.eachDependency {
                if (requested.group == SecurityOverrides.COMMONS_LANG_GROUP &&
                    requested.name == SecurityOverrides.COMMONS_LANG3_ARTIFACT
                ) {
                    useVersion(commonsLang3SecurityVersion)
                    because(SecurityOverrides.COMMONS_LANG3_REASON)
                }
                if (requested.group == SecurityOverrides.GUAVA_GROUP &&
                    requested.name == SecurityOverrides.GUAVA_ARTIFACT
                ) {
                    useVersion(guavaSecurityVersion)
                    because(SecurityOverrides.GUAVA_REASON)
                }
            }
        }

        if (name == "ktlint") {
            resolutionStrategy.eachDependency {
                if (requested.group == SecurityOverrides.LOGBACK_GROUP &&
                    requested.name in SecurityOverrides.LOGBACK_ARTIFACTS
                ) {
                    useVersion(logbackSecurityVersion)
                    because(SecurityOverrides.LOGBACK_REASON)
                }
            }
        }
    }
}

private fun VersionCatalog.requiredVersion(alias: String): String =
    findVersion(alias)
        .get()
        .requiredVersion

fun Project.configureJsSecurityOverrides(versionCatalog: VersionCatalog) {
    val ajvSecurityVersion = versionCatalog.requiredVersion("ajv-security")
    val minimatchSecurityVersion = versionCatalog.requiredVersion("minimatch-security")
    val yarnRootExtension = resolveYarnRootExtension()

    // Keep Kotlin/JS transitive dependencies patched in kotlin-js-store/yarn.lock.
    yarnRootExtension.applyResolution("ajv", ajvSecurityVersion)
    yarnRootExtension.applyResolution("minimatch", minimatchSecurityVersion)
}

private fun Project.resolveYarnRootExtension(): Any {
    val yarnRootClass =
        sequenceOf(
            Thread.currentThread().contextClassLoader,
            javaClass.classLoader,
            this::class.java.classLoader,
        ).filterNotNull()
            .mapNotNull { classLoader ->
                runCatching {
                    Class.forName(
                        "org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension",
                        true,
                        classLoader,
                    )
                }.getOrNull()
            }.firstOrNull()
            ?: error("Unable to load YarnRootExtension class from available classloaders")
    return try {
        val getMethod = yarnRootClass.getMethod("get", Project::class.java)
        getMethod.invoke(null, this)
    } catch (_: NoSuchMethodException) {
        val companion = yarnRootClass.getDeclaredField("Companion").get(null)
        val getMethod =
            companion.javaClass.methods.firstOrNull { method ->
                method.name == "get" &&
                    method.parameterTypes.contentEquals(
                        arrayOf(Project::class.java),
                    )
            } ?: error("Unable to locate YarnRootExtension#get(Project) method")
        getMethod.invoke(companion, this)
    }
}

private fun Any.applyResolution(
    dependencyName: String,
    version: String,
) {
    val resolutionMethod =
        javaClass.methods.firstOrNull { method ->
            method.name == "resolution" &&
                method.parameterTypes.contentEquals(
                    arrayOf(String::class.java, String::class.java),
                )
        } ?: error("Unable to locate YarnRootExtension#resolution(String, String) method")
    resolutionMethod.invoke(this, dependencyName, version)
}
