#!/bin/bash

model=$1
repetitions=$2
timeout=$3
provider=$4
temperature=$5
max_tokens=$6

if [ -z "$model" ] || [ -z "$repetitions" ] || [ -z "$timeout" ] || \
   [ -z "$provider" ] || [ -z "$temperature" ] || [ -z "$max_tokens" ]; then
    echo "Usage: $0 <model> <repetitions> <timeout> <provider> <temperature> <max_tokens>"
    exit 1
fi

for i in $(seq 1 "$repetitions"); do
    echo "Running experiment with model $model (run $i/$repetitions)"

    timeout "${timeout}s" ../gradlew runExperiment --args="--lm-server-url=${provider} --model-id=${model} --temperature=${temperature} --max-tokens=${max_tokens} --log-dir=experiments --log-to-console --log-to-file" || exit_code=$?

    if [ "$exit_code" -eq 124 ]; then
        echo "Timeout reached"
    elif [ "$exit_code" -ne 0 ]; then
        echo "Error occurred during run $i with exit code $exit_code"
        exit "$exit_code"
    fi
done