package it.unibo.jakta.llm

data class PromptGenerator(
    val actions: String,
    val remarks: String = "None.",
    val memory: String = "None.",
    val beliefs: String = "None.",
    val goal: String,
) {
    fun buildPrompt(): String =
        """
        | $bdiAgentPlannerProfile
        | 
        | $planGenerationInstruction
        | 
        | $planTemplate
        | 
        | Available actions:
        | ```
        | $actions
        | ```
        | 
        | Other available goals:
        | ```
        | None.
        | ```
        | 
        | Your beliefs:
        | ```
        | $beliefs
        | ```
        |  
        | Remarks:
        | ```
        | $remarks
        | ```
        | 
        | The goal to solve is:
        | ```
        | $goal
        | ```
        | 
        | Remember that in order to generate valid prolog code you must end each clause with a dot.
        | 
        | Now take a deep breath and take your time to think. 
        | Write the current step using the schema below.
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

        val planGenerationInstruction =
            """
            | You will be given a goal to achieve as best you can. To solve the goal, you must proceed in
            | a series of steps, in a cycle of "thought:" and "code:" sequences.
            | 
            | Your objective is to provide a plan, in the form of a set of goals that allow to complete the given goal.
            | To solve the goal, you must proceed in a series of steps, in a cycle of "thought:" and "plan:" sequences.
            | 
            | At each step, in the "thought:" section, you should first explain your reasoning towards
            | building the plan and the actions that you want to use or the goals that need to be achieved.
            | Then in the "planBody:" and "planGuard:" sections, you should write the code of the plan.
            """.trimMargin("| ")

        val planTemplate: String =
            """
            | The plan must be defined using two fields:
            | 
            | - body: it holds the actions and/or the goals to achieve in order to satisfy the goal of the plan;
            | - guard: it holds the preconditions that must be true in order to execute the plan.
            | 
            | In the body section provide the goals as a snippet of prolog code:
            | 
            | ```prolog
            | put the goals here
            | ```
            |
            | If you think that the plan has preconditions, put them in a snippet of prolog code too.
            """.trimMargin("| ")
    }
}
