plugins {
    alias(libs.plugins.kotlinx)
}

dependencies {
    api(project(":jakta-state-machine"))

    api(libs.tuprolog.core)
    api(libs.tuprolog.theory)
    api(libs.tuprolog.parser.theory)
    api(libs.tuprolog.parser.core)
    api(libs.tuprolog.oop.lib)
    api(libs.tuprolog.solve.classic)

    implementation(libs.bundles.kotlin.logging)
    implementation(libs.bundles.koin)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.bundles.kotlin.testing)
    annotationProcessor(libs.log4j.core)
}
