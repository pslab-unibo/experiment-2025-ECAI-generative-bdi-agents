# Gradle plugins

The `python-dvc` convention plugin helps manage a Python virtual environment and DVC (Data Version Control) setup inside a Gradle project. It is designed for use in projects that combine JVM tooling with Python-based workflows, such as machine learning or data engineering pipelines.

## Requirements

- Gradle with Kotlin DSL
- Python 3.x installed and available in system PATH

## Usage

Create a virtual environment:

```shell
./gradlew createVenv
```

Install DVC:

```shell
./gradlew installDvc
```

To run python command:

```shell
./gradlew python -Pargs="--version"
```

To run a DVC command:

```shell
./gradlew dvc -Pargs="status"
```

To initialize the DVC repo:

```shell
./gradlew dvc -Pargs="init"
# use --subdir if initializing inside a subdirectory of a parent SCM repository
./gradlew dvc -Pargs="init --subdir"
```
