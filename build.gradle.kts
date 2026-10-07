import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar
import isocodes.gen.GenerateIsoCodesTask

plugins {
    `java-library`
    id("com.vanniktech.maven.publish") version "0.37.0"
}

val upstreamVersion = providers.gradleProperty("isoCodesVersion").get()
version = upstreamVersion

repositories {
    mavenCentral()
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

val generateIsoCodes = tasks.register<GenerateIsoCodesTask>("generateIsoCodes") {
    isoCodesVersion = upstreamVersion
    basePackage = providers.gradleProperty("basePackage")
    downloadDirectory = layout.buildDirectory.dir("iso-codes-json")
    outputDirectory = layout.buildDirectory.dir("generated/sources/iso-codes")
}

sourceSets.main {
    java.srcDir(generateIsoCodes)
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 17
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xlint:all")
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("basePackage", providers.gradleProperty("basePackage").get())
}

mavenPublishing {
    configure(JavaLibrary(javadocJar = JavadocJar.Javadoc(), sourcesJar = SourcesJar.Sources()))
    publishToMavenCentral()
    // Signing keys only exist in CI; local publishToMavenLocal works unsigned.
    if (providers.gradleProperty("signingInMemoryKey").isPresent) {
        signAllPublications()
    }

    coordinates(group.toString(), "iso-codes", upstreamVersion)

    pom {
        name = "iso-codes-java"
        description = "Java enums and classes generated from Debian's iso-codes (ISO 639, 3166, 4217, 15924) $upstreamVersion."
        url = providers.gradleProperty("projectUrl")
        inceptionYear = "2026"
        licenses {
            license {
                name = "LGPL-2.1-or-later"
                url = "https://spdx.org/licenses/LGPL-2.1-or-later.html"
                distribution = "repo"
            }
        }
        developers {
            developer {
                id = providers.gradleProperty("developerId")
                name = providers.gradleProperty("developerName")
                url = providers.gradleProperty("developerUrl")
            }
        }
        scm {
            url = providers.gradleProperty("projectUrl")
            connection = providers.gradleProperty("projectUrl").map { "scm:git:$it.git" }
            developerConnection = providers.gradleProperty("projectUrl").map { "scm:git:$it.git" }
        }
    }
}

tasks.withType<Jar>().configureEach {
    from("LICENSE") { into("META-INF") }
}
