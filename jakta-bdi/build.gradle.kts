dependencies {
    api(project(":jakta-llm"))
    api(project(":jakta-state-machine"))
    api(libs.tuprolog.core)
    api(libs.tuprolog.theory)
    api(libs.tuprolog.parser.theory)
    api(libs.tuprolog.parser.core)
    api(libs.tuprolog.oop.lib)
    api(libs.tuprolog.solve.classic)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.logging)
    implementation(libs.logback)
    implementation(libs.slf4jOverLogback)
}
