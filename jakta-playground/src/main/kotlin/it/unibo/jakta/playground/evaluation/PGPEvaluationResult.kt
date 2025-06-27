package it.unibo.jakta.playground.evaluation

import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationConfig
import kotlinx.serialization.Serializable
import java.io.OutputStream

@Serializable
data class PGPEvaluationResult(
    val pgpId: String,
    val parsedPlans: List<Plan>,
    val amountGeneratedPlans: Int,
    val averageAmountBeliefs: Double,
    val averageAmountOperations: Double,
    val amountGeneralPlan: Int,
    val amountInventedGoals: Int,
    val amountInventedBeliefs: Int,
    val amountUselessPlans: Int,
    val amountNotParseablePlans: Int,
    val amountInadequateUsageGoals: Int,
    val amountInadequateUsageBeliefs: Int,
    val amountInadequateUsageActions: Int,
    val timeUntilCompletion: Long? = null,
    val executable: Boolean,
    val achievesGoal: Boolean,
    val genConfig: LMGenerationConfig? = null,
) {
    override fun toString(): String =
        buildString {
            appendLine("Amount of generated plans: $amountGeneratedPlans")
            appendLine("Average amount of beliefs per plan context: $averageAmountBeliefs")
            appendLine("Average amount of operations per plan body: $averageAmountOperations")
            appendLine("Amount of generated plans which are general: $amountGeneralPlan")
            appendLine("Amount of invented goals: $amountInventedGoals")
            appendLine("Amount of invented beliefs: $amountInventedBeliefs")
            appendLine("Amount of useless generated plans: $amountUselessPlans")
            appendLine("Number of not-parseable plans: $amountNotParseablePlans")
            appendLine("Amount of inadequate usage of admissible goals: $amountInadequateUsageGoals")
            appendLine("Amount of inadequate usage of admissible beliefs: $amountInadequateUsageBeliefs")
            appendLine("Amount of inadequate usage of actions: $amountInadequateUsageActions")
            timeUntilCompletion?.let { appendLine("Time to goal achievement: $timeUntilCompletion") }
            appendLine("Executable: $executable")
            append("Achieves goal: $achievesGoal")
        }

    companion object {
        fun writeCsv(
            results: List<PGPEvaluationResult>,
            outputStream: OutputStream,
        ) {
            outputStream.bufferedWriter().use { writer ->
                // Header
                writer.write(
                    """
                    "PgpId",
                    "ModelId",
                    "Temperature",
                    "MaxTokens",
                    "Provider",
                    "PromptType",
                    "AmountGeneratedPlans",
                    "AverageAmountBeliefs",
                    "AverageAmountOperations",
                    "AmountGeneralPlan",
                    "AmountInventedGoals",
                    "AmountInventedBeliefs",
                    "AmountUselessPlans",
                    "AmountNotParseablePlans",
                    "AmountInadequateUsageGoals",
                    "AmountInadequateUsageBeliefs",
                    "AmountInadequateUsageActions",
                    "TimeUntilCompletion",
                    "Executable",
                    "AchievesGoal"
                    """.trimIndent().replace("\n", "").replace(" ", ""),
                )
                writer.newLine()

                // Data rows
                results.forEach { result ->
                    writer.write(
                        """
                        "${result.pgpId}",
                        "${result.genConfig?.modelId}",
                        "${result.genConfig?.temperature}",
                        "${result.genConfig?.maxTokens}",
                        "${result.genConfig?.lmServerUrl}",
                        "${result.genConfig?.promptType}",
                        "${result.amountGeneratedPlans}",
                        "${result.averageAmountBeliefs}",
                        "${result.averageAmountOperations}",
                        "${result.amountGeneralPlan}",
                        "${result.amountInventedGoals}",
                        "${result.amountInventedBeliefs}",
                        "${result.amountUselessPlans}",
                        "${result.amountNotParseablePlans}",
                        "${result.amountInadequateUsageGoals}",
                        "${result.amountInadequateUsageBeliefs}",
                        "${result.amountInadequateUsageActions}",
                        "${result.timeUntilCompletion}",
                        "${result.executable}",
                        "${result.achievesGoal}"
                        """.trimIndent().replace("\n", "").replace(" ", ""),
                    )
                    writer.newLine()
                }
            }
        }
    }
}
