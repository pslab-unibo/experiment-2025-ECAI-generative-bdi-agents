import org.gradle.api.tasks.JavaExec

tasks.register<JavaExec>("runExperiment") {
    group = "DVC"
    description = "Task to run a single experiment instance"

    // This will need to be configured in the project's build.gradle.kts
    // mainClass.set("your.package.MainClass")
    // classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<ExperimentTask>("runExperiments") {
    group = "DVC"
    description = "Task to run experiments (replaces runner.sh)"
}
