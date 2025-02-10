class Step {
  goalName string
  @description(#"
    The identifier of the goal to complete, in camel case.
  "#)
  thought string
  @description(#"
    The reasoning of the agent.
  "#)
  planBody string
  @description(#"
    The list of goals and/or actions needed to complete the goal of the plan.
  "#)
  planGuard string
  @description(#"
    The preconditions that need to be true in order to run the plan. Leave empty if none are needed.
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

      Your objective is to provide a plan, in the form of a set of goals that allow to complete the given goal.
      To solve the goal, you must proceed in a series of steps, in a cycle of "thought:" and "plan:" sequences.

      At each step, in the "thought:" section, you should first explain your reasoning towards
      building the plan and the actions that you want to use or the goals that need to be achieved.
      Then in the "plan:" section, you should write the code of the plan in Prolog.

      The plan must be defined using two fields:

      - body: it holds the actions or the goals to achieve in order to satisfy the goal of the plan;
      - guard: it holds the preconditions that must be true in order to execute the plan.
      
      In the body section provide the goals as a snippet of prolog code:

      ```prolog
      put the goals here
      ```

      If you think that the plan has preconditions, put them in a snippet of prolog code too.
      
      Available actions:
      ```
      %! Prints a message to the console and a newline.
      print(+message:string).

      %! Stops the agent, preventing the execution of subsequent actions.
      stop().
      ```

      Other available goals:
      ```
      None.
      ```

      ## Remarks

      The agent must stop once the goal is completed.

      The goal to solve is:
      ```
      Print numbers 1...3 in this order and stop the agent.
      ```

      Now take a deep breath and take your time to think. 
      Write the current step using the schema below.
      "#
  }
}
