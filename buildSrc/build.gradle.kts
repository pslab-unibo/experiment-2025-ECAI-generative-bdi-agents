plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlinx)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(libs.kaml)
}
