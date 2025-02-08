dependencies {
    api(project(":jakta-bdi"))
    api(libs.tuprolog.dsl.theory)
    api(libs.tuprolog.dsl.core)

    implementation(libs.kotlin.logging)
    implementation(libs.logback)
    implementation(libs.slf4jOverLogback)
}
