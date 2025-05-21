dependencies {
    testImplementation(libs.konsist)
    testImplementation(libs.kotlinx.serialization.json)

    api(project(":jakta-state-machine"))
    api(project(":jakta-plan-generation"))
    api(project(":jakta-bdi"))
    api(project(":jakta-dsl"))
}
