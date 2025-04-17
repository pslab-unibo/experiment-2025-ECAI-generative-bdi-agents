package it.unibo.jakta.generationstrategies.lm.pipeline.parsing.tagprocessor

import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ParsedStatement.PlanStatement
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.PlanStreamingParser

class PlanProcessor : TagProcessor {
    private val planParser = PlanStreamingParser.of()
    private val plans = mutableListOf<PlanStatement>()

    override fun process(content: String): Boolean {
        planParser.parse(content)
        val plan = planParser.getParsedPlan()

        if (plan != null) {
            val planStatement = PlanStatement(
                content,
                plan.trigger,
                plan.guards,
                plan.goals,
            )
            plans.add(planStatement)
            planParser.reset()
        }

        return plan != null
    }

    override fun getItems(): List<PlanStatement> = plans.toList()
}
