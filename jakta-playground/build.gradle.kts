import java.util.Properties

plugins {
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx)
    id("python-dvc")
}

repositories {
    maven("https://maven.tryformation.com/releases") {
        content {
            includeGroup("com.jillesvangurp")
        }
    }
}

dependencies {
    api(project(":jakta-dsl"))
    api(project(":jakta-plan-generation"))

    api(libs.kotlin.coroutines)
    api(libs.ktor.network)

    implementation(libs.bundles.kotlin.testing)
    implementation(libs.bundles.kotlin.logging)
    implementation(libs.openai)
    implementation(libs.clikt)
    implementation(libs.bundles.koin)
    implementation(libs.ktsearch)
}

tasks.register<JavaExec>("runExperiment") {
    val keystoreFile = project.rootProject.file(".env")
    val properties = Properties()
    properties.load(keystoreFile.inputStream())

    environment = mapOf("API_KEY" to properties.getProperty("API_KEY"))
    description = "Run the explorer agent sample with the given experimental config."
    group = "application"

    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "${project.group}.playground.explorer.ExplorerRunnerKt"
}

tasks.register<JavaExec>("replayExperiment") {
    description = "Run the explorer agent sample by reusing already generated responses."
    group = "application"

    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "${project.group}.playground.explorer.ExplorerExperimentReplayerKt"
}

tasks.register<JavaExec>("runBaseline") {
    description = "Run the explorer agent sample with the baseline plans."
    group = "application"

    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "${project.group}.playground.explorer.BaselineExplorerRunnerKt"
}

tasks.register<JavaExec>("analyzePGP") {
    description = "Evaluate each PGP attempt."
    group = "application"

    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "${project.group}.playground.evaluation.scripts.AnalyzePGPKt"
}

tasks.register<JavaExec>("runDomesticRobot") {
    description = "Run the domestic robot application."
    group = "application"

    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "${project.group}.playground.domesticrobot.DomesticRobotRunnerKt"
}
