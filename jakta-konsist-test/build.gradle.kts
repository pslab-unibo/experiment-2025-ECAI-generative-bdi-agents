dependencies {
    testImplementation(libs.konsist)
    testImplementation(libs.kotlinx.serialization.json)

    testImplementation(project(":jakta-state-machine"))
    testImplementation(project(":jakta-plan-generation"))
    testImplementation(project(":jakta-bdi"))
    testImplementation(project(":jakta-dsl"))
}
