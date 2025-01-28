plugins {
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx)
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.contentnegotiation)
    implementation(libs.ktor.client.gson)
    implementation(libs.ktor.serialization.gson)

    implementation(libs.logback)
    implementation(libs.kotlin.logging)
    implementation(libs.logback)
    implementation(libs.slf4jOverLogback)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.compiler.embeddable)
}

val generatedSourcesDir = layout.buildDirectory.dir("generated/source/openapi/main/kotlin")
val sourceSetGeneratedDir = layout.buildDirectory.dir("generated/source/openapi/main/kotlin/src/main/kotlin")

openApiGenerate {
    generatorName.set("kotlin")
    library.set("jvm-ktor")
    generateApiTests.set(false)
    generateModelTests.set(false)
    inputSpec.set("$projectDir/src/main/baml/baml_client/openapi.yaml")
    outputDir.set(generatedSourcesDir.get().asFile.absolutePath)
    apiPackage.set("org.openapitools.client.api")
    modelPackage.set("org.openapitools.client.models")
    packageName.set("org.openapitools.client")
    additionalProperties.set(
        mapOf(
            "java8" to "true",
            "serializationLibrary" to "gson",
        ),
    )
}

sourceSets["main"].kotlin {
    srcDir(sourceSetGeneratedDir)
}

ktlint {
    filter {
        exclude { it.file.path.contains("/generated/") }
    }
}

tasks.register<Exec>("bamlGenerate") {
    description = "Run baml generator."
    group = "build"

    standardOutput = System.out
    commandLine(".venv/bin/baml-cli", "generate", "--from", "src/main/baml/baml_src/")
}

tasks.named("openApiGenerate") {
    dependsOn("bamlGenerate")
}
