# jakta-playground

This module provides a toy application that makes an agent print a sequence of numbers, starting from a specification that states the goal to achieve but does not provide a plan to satisfy it. 

An LLM is called to generate the plan. 
Its result is stored in a file and added to the agent at runtime, by using the embedded Kotlin compiler to compile the source to bytecode and by loading the resulting `.class` file in the classloader.

## Prerequisites

Follow the prerequisites listed under the `jakta-llm` module.

### Local setup

Llama.cpp is used to serve models locally. Download a LLM in GGUF format and put it in the `models/` directory:

```shell
# ~ 5 GB
wget -P models/ https://huggingface.co/unsloth/DeepSeek-R1-Distill-Qwen-7B-GGUF/resolve/main/DeepSeek-R1-Distill-Qwen-7B-Q4_K_M.gguf?download=true
```

Update the `MODEL` variable and the other parameters in the `.env` file accordingly.

### Use with an external provider

Choose a different client in the `jakta-llm/src/main/baml/planner.baml` file.
For a non-exhaustive list of available clients, refer to the `clients.baml` file.

Add the relevant API key in the `.env` file.

## Running the application

Start the docker compose application:

```shell
# For local use
docker compose -f compose.yml -f compose.llama.yml up 

# For use with an external provider
docker compose up
```

To generate the openApi spec used by the Kotlin client:

```shell
./gradlew openApiGenerate
```

To run the mas:

```shell
./gradlew runMAS
```