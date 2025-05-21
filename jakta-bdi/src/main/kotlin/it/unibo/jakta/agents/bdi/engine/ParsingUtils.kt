package it.unibo.jakta.agents.bdi.engine

object ParsingUtils {
    enum class TriggerKeyword {
        Achieve,
        AchieveFailure,
        Test,
        TestFailure,
        AddBelief,
        RemoveBelief,
        UpdateBelief,
        ;

        companion object {
            fun extractTriggerType(input: String): TriggerKeyword? =
                when {
                    input.startsWith("achieve") -> Achieve
                    input.startsWith("achieve failure") -> AchieveFailure
                    input.startsWith("test") -> Test
                    input.startsWith("test failure") -> TestFailure
                    input.startsWith("belief addition") -> AddBelief
                    input.startsWith("belief removal") -> RemoveBelief
                    input.startsWith("belief update") -> UpdateBelief
                    else -> null
                }
        }
    }

    enum class GoalKeyword {
        Achieve,
        Test,
        Spawn,
        Generate,
        Add,
        Remove,
        Update,
        Execute,
        Iact,
        ;

        companion object {
            fun extractGoalType(input: String): GoalKeyword? =
                when {
                    input.startsWith("achieve") -> Achieve
                    input.startsWith("test") -> Test
                    input.startsWith("spawn") -> Spawn
                    input.startsWith("generate") -> Generate
                    input.startsWith("add") -> Add
                    input.startsWith("remove") -> Remove
                    input.startsWith("update") -> Update
                    input.startsWith("execute") -> Execute
                    input.startsWith("iact") -> Iact
                    else -> null
                }
        }
    }
}
