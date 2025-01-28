package it.unibo.jakta.llm.input

data class Prompt(
    val actions: String,
    val observations: String = "None.",
    val memory: String = "None.",
    val beliefs: String = "None.",
    val goalName: String,
    val goalDescription: String,
) {
    fun buildPrompt(): String =
        """
        | $bdiAgentPlannerProfile
        | 
        | $reactGoalToPlan
        | 
        | ${planTemplate(goalName)}
        | 
        | $planExamples
        | 
        | Signature of the available actions:
        | ```
        | $actions
        | ```
        |  
        | ## Observations
        | 
        | $observations
        | 
        | ## Previous steps information
        | 
        | $memory
        | 
        | ## Current step information
        | 
        | Your beliefs:
        | ```
        | $beliefs
        | ```
        | 
        | The goal to solve, named `$goalName`, is:
        | ```
        | $goalDescription
        | ```
        | 
        | Now take a deep breath and take your time to think. Write the current step using the schema below.
        |     
        """.trimMargin("| ")

    companion object Prompt {
        val bdiAgentPlannerProfile =
            """
           | You are an expert assistant who can provide plans to any Belief Desire Intention (BDI) 
           | agent using code blobs. A BDI agent has a set of beliefs, goals, plans and actions.
           | 
           | - Beliefs are the information that the agent has about the world.
           | - Goals are the objectives that the agent wants to achieve.
           | - Actions are the operations that the agent can perform.
           | - Plans are the sequences of actions that the agent can execute to achieve the goals.
            """.trimMargin("| ")

        val reactGoalToPlan =
            """
            | You will be given a goal to achieve as best you can. To solve the goal, you must proceed in
            | a series of steps, in a cycle of "thought:" and "code:" sequences.
            | You can use your beliefs to inform your decisions.
            |  
            | At each step, in the "thought:" section, you should first explain your reasoning towards
            | building the plan and the signature of the actions that you want to use.
            | Then in the "code:" section, you should write the code of the plan in Kotlin.
            """.trimMargin("| ")

        val planTemplate: (String) -> String = { goalName ->
            """
            | The plan must be defined using this template:
            | 
            | ```kotlin
            |     +achieve("$goalName") onlyIf {
            |        // Add zero or more beliefs here
            |     } then {
            |      // Add at least an action here
            |     }
            | ```
            | 
            | otherwise, if there are no beliefs needed you can use the following template:
            | 
            | ```kotlin
            | +achieve("$goalName") then {
            |   // Add at least an action here
            | }
            | ```
            | 
            | In order to generate a valid plan, you need to provide a set of beliefs
            | that must be true in order to execute the plan, and a set of actions that will be executed.
            """.trimMargin("| ")
        }

        val planExamples =
            """
            | Below you can see some examples of plans and of the goals they achieve:
            | 
            | ```kotlin
            | +achieve("printMessage") then {
            |     Print("Hello, World!")
            |     execute("stop")
            | }
            | ```
            """.trimMargin("| ")
    }
}
