package it.unibo.jakta.playground

import it.unibo.jakta.agents.bdi.dsl.agent
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.goals.Achieve
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.generationstrategies.lm.dsl.DSLExtensions.givenLMConfig
import it.unibo.jakta.generationstrategies.lm.dsl.DSLExtensions.oneStepGeneration
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PromptBuilder
import it.unibo.tuprolog.core.Atom

fun main() {
    val goal = "lorem ipsum"
    val promptBuilder = PromptBuilder.of(withSubgoals = false)

    val agent = agent("Test") {
        oneStepGeneration {}

        actions {
            action("print", "message") {
                println(arguments[0].castToAtom().toString())
            }
        }

        beliefs {
            fact { "test" }
            admissible {
                fact { "test" }
            }
        }

        plans {
            +achieve("Test") onlyIf {
                "test" and "taster"
            } givenLMConfig {
                remark("Test String")
            }

            +achieve("Test") givenLMConfig {
                remark("Test String")
            }

            +achieve("Test") givenLMConfig {
                remark("Test String")
            } then {
                execute("print"("Hello World"))
            }

            +achieve("Tester") onlyIf {
                "tester" and "test"
            } givenLMConfig {
                remark("Test String")
            } then {
                execute("print"("Hello World"))
            }
        }

        goals {
            admissible {
                achieve("test nl goal")
            }
        }
    }
    val environment = Environment.of()

    val prompt = promptBuilder.buildPrompt(
        GeneratePlan.of(Achieve.of(Atom.of(goal))),
        agent.context,
        environment.externalActions.values.toList(),
    )
    println(prompt.content)
}
