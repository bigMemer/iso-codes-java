plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.3")
    implementation("com.palantir.javapoet:javapoet:0.20.0")
}
