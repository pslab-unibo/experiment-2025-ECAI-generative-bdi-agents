dependencies {
    api(project(":jakta-dsl"))
    implementation(libs.kotlin.coroutines)

    implementation(libs.logback)
    implementation(libs.kotlin.logging)
    implementation(libs.logback)
    implementation(libs.slf4jOverLogback)
}

tasks.register<JavaExec>("runMAS") {
    description = "Run the multi-agent system."
    group = "custom"

    standardOutput = System.out
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "${project.group}.playground.OrderedPrintsExampleKt"
}
