plugins {
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx)
}

dependencies {
    api(project(":jakta-dsl"))
    api(project(":jakta-bdi"))
    api(libs.bundles.kotlin.logging)

    implementation(libs.kotlin.coroutines)
    implementation(libs.openai)
    implementation(libs.bundles.kotlin.ktor)
}
