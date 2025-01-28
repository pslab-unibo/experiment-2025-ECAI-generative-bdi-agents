class Step {
  thought string
  @description(#"
    The reasoning of the agent.
  "#)
  code string
  @description(#"
    The plan written in the given Kotlin template.
  "#)
}

function AskForPlanGeneration(context: string) -> Step {
  client HermesLocalNetwork
  prompt #"
    {{ context }}

    {{ ctx.output_format }}
  "#
}

test requestPlanGeneration {
  functions [AskForPlanGeneration]
  args {
    context #"
      You are an expert assistant who can provide plans to any Belief Desire Intention (BDI) agent using code blobs. A BDI agent has a set of beliefs, goals, plans and actions.

      - Beliefs are the information that the agent has about the world.
      - Goals are the objectives that the agent wants to achieve.
      - Actions are the operations that the agent can perform.
      - Plans are the sequences of actions that the agent can execute to achieve the goals.

      You will be given a goal to achieve as best you can. To solve the goal, you must proceed in
      a series of steps, in a cycle of "thought:" and "code:" sequences.
      You can use your beliefs to inform your decisions.

      At each step, in the "thought:" section, you should first explain your reasoning towards
      building the plan and the signature of the actions that you want to use.
      Then in the "code:" section, you should write the code of the plan in Kotlin.

      The plan must be defined using this template:

      ```kotlin
      +achieve("orderedGoalExecution") onlyIf {
          // Add zero or more beliefs here
      } then {
          // Add at least an action here
      }
      ```
      
      In order to generate a valid plan, you need to provide a set of beliefs
      that must be true in order to execute the plan, and a set of actions that will be executed.
      
      If no beliefs are needed, you can leave the `onlyIf` block empty.

      Below you can see some examples of plans and of the goals they achieve:

      ```kotlin
      +achieve("printMessage") then {
          Print("Hello, World!")
          execute("stop")
      }
      ```

      Signature of the available actions:
      ```
      /**
        * Prints a message to the console and a newline.
        */
      Print: (message: String) -> Unit

      /**
        * Stops the agent, preventing the execution of subsequent actions.
        */
      execute("stop"): () -> Unit
      ```

      ## Observations

      The agent must stop once the goal is completed.

      ## Previous steps information

      Your memories:
      ```
      none
      ```

      ## Current step information

      Your beliefs:
      ```
      none
      ```

      The goal to solve, named `orderedGoalExecution`, is:
      ```
      Create a plan that prints 2 3 1 3 in this order.
      ```

      Now take a deep breath and take your time to think. Write the current step using the schema below.
      "#
  }
}
