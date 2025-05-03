package it.unibo.jakta.playground

import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.generationstrategies.lm.dsl.DSLExtensions.oneStepGeneration
import it.unibo.jakta.generationstrategies.lm.pipeline.filtering.DefaultFilters
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.DefaultPromptBuilder
import it.unibo.jakta.playground.MockGenerationStrategy.createOneStepStrategyWithMockedAPI
import it.unibo.jakta.playground.experiment.NameGenerator
import it.unibo.jakta.playground.explorer.ExplorerBot.explorerBot
import it.unibo.jakta.playground.explorer.ExplorerBot.gridWorld

val txt1 = """
```yaml
EVENT: achieve reach(Object)
CONDITIONS:
  - ¬there_is(Object, here)
  - object(Object)
OPERATIONS:
  - test getDirectionToMove(Direction)
  - execute move(Direction)
  - achieve reach(Object)
---
EVENT: achieve reach(Object)
CONDITIONS:
  - there_is(Object, here)
OPERATIONS:
  - <none>
```

```yaml
- goal: `reach(Object)`
  purpose: reach a situation where Object is in the position of the agent (i.e., there_is(Object, here))
- belief: `there_is(Object, here)`
  purpose: Object is at the agent's current position
- belief: `getDirectionToMove(Direction)`
  purpose: Direction is a direction free of obstacles where the agent can move
```
""".trimIndent()

val txt2 = """
```yaml
EVENT: test getDirectionToMove(D)
CONDITIONS:
  - direction(D)
  - free(D)
OPERATIONS:
  - <none>
```

---

```yaml
# Invented admissible goals and beliefs

<none>
```
""".trimIndent()

// "experiments/anthropic/claude-3.7-sonnet:thinking/$expName"
// "experiments/google/gemini-pro-2.5:preview/$expName"
// "experiments/openai/gpt-4.1:preview/$expName"
fun main() {
    val nameGenerator = NameGenerator()
    val expName = nameGenerator.randomName()
    mas {
        loggingConfig = LoggingConfig(
            logDir = "experiments/openai/gpt-4.1:preview/$expName",
//            logToFile = true
        )
        gridWorld()
        explorerBot(strategy = createOneStepStrategyWithMockedAPI(listOf(txt1, txt2)))

        oneStepGeneration {
            contextFilter = DefaultFilters.defaultFilter
            promptBuilder = DefaultPromptBuilder.descriptivePrompt
        }
    }.start()
}
