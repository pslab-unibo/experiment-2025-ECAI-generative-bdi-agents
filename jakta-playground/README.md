# jakta-playground

## Prerequisites

1. Add an openrouter API key to a `.env` file at the root of the repo (see `.env.template` for reference).

2. Run `chmod +x runner.sh` to make the script executable.

## Evaluation

To run the DVC pipeline:

```shell
../gradlew dvc -Pargs="repro"
```

This will start the evaluation with the parameters
specified in the `params.yaml` file and by running the `runExperiment` gradle task.

The results will be logged under the `./experiments/` directory.

### Directory structure

The first level contains the name of the provider and the second level the name of the model.
For each model, one or more runs are available. 
For each of them, there is a directory for each agent of the mas.
For each agent, there is an optional chat folder to store the prompt and the response
of each PGP attempt.

```
.
├── google
│   └── gemini-2.0-flash-exp:free
│       └── happy_wozniak
│           ├── ExplorerBot
│           │   ├── chat
│           │   │   └── 54c86a3a-1057-4b53-8f3d-931eba6e91f4.jsonl
│           │   └── ExplorerBot.jsonl
│           ├── mas.jsonl
│           └── timeout
│               └── timeout.jsonl
└── meta-llama
    └── llama-3.3-70b-instruct:free
        └── inspiring_mayer
            ├── ExplorerBot
            │   ├── chat
            │   │   └── c80239ee-be81-45e9-91cd-1b6c0b0e8b35.jsonl
            │   └── ExplorerBot.jsonl
            ├── mas.jsonl
            └── timeout
                └── timeout.jsonl
```
