dependencies {
    api(project(":jakta-bdi"))

    api(libs.tuprolog.dsl.theory)
    api(libs.tuprolog.dsl.core)

    implementation(libs.bundles.kotlin.logging)
}
