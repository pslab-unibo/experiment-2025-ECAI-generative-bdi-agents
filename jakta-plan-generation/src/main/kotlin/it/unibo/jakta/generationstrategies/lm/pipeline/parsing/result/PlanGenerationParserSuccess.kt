package it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.LiteratePlan
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult.ParserSuccess
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult.PlanGenerationParserResult
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth

sealed interface PlanGenerationParserSuccess : ParserSuccess, PlanGenerationParserResult {
    data class CompositeParserSuccess(
        val newStep: NewStep? = null,
        val newPlans: List<NewPlan>? = null,
    ) : PlanGenerationParserSuccess {
        override val rawContent: String = StringBuilder().apply {
            if (newStep != null) {
                appendLine(newStep.rawContent)
            }
            if (newPlans != null) {
                appendLine(newPlans.joinToString { it.rawContent })
            }
        }.toString()
    }

    data class NewStep(
        val goal: Goal,
        override val rawContent: String,
    ) : PlanGenerationParserSuccess {
        companion object {
            fun of(goal: Goal): NewStep =
                NewStep(goal, formatter.format(goal.value))
        }
    }

    data class NewPlan(
        val plan: LiteratePlan,
        override val rawContent: String,
    ) : PlanGenerationParserSuccess {
        companion object {
            fun of(
                id: PlanID? = null,
                trigger: Trigger,
                guard: Struct = Truth.TRUE,
                goals: List<Goal>,
                literateTrigger: String = "",
                literateGuard: String = "",
                literateGoals: String = "",
                rawContent: String = "",
            ): NewPlan = NewPlan(
                LiteratePlan.of(
                    id ?: PlanID.of(trigger, guard),
                    trigger,
                    guard,
                    goals,
                    literateTrigger,
                    literateGuard,
                    literateGoals,
                ),
                rawContent,
            )
        }
    }

    data class EmptyResponse(
        override val rawContent: String,
    ) : PlanGenerationParserSuccess

    data class RequestFailure(
        override val rawContent: String,
    ) : PlanGenerationParserSuccess
}
