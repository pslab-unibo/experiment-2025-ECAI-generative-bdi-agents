class GoalProposal {
  reasoning string
  goals string[]
}


function AskForGoalGeneration(context: string, format: string, task: string) -> GoalProposal {
  client HermesLocalhost
  prompt #"
    {{ context }}

    {{ format }}

    Answer using this template:

    {{ ctx.output_format }}
    "#
}
