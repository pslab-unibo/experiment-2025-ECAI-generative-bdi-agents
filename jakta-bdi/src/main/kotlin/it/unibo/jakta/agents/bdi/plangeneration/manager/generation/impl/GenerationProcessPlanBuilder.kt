package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.plangeneration.Common.getStrategyFromID
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.PlanFactory
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.tuprolog.core.Truth

class GenerationProcessPlanBuilder(private val logger: KLogger?) {
    fun findOrCreatePlan(
        genGoal: Generate,
        intention: Intention,
        context: AgentContext,
    ): PartialPlan {
        val genPlanID = context.generationProcesses
            .filter { it.key.trigger.value == genGoal.value }
            .keys
            .firstOrNull()

        return when {
            genPlanID != null -> context.planLibrary.getPlan(genPlanID) as? PartialPlan
                ?: createNewPlan(genPlanID, genGoal, context).also {
                    logger?.info { "Creating new plan ${formatter.format(genPlanID.trigger.value)}" }
                }
            else -> createNewPlan(intention.currentPlan(), genGoal, context).also {
                logger?.info { "Creating new plan ${formatter.format(it.trigger.value)}" }
            }
        }
    }

    private fun createNewPlan(
        planID: PlanID,
        genGoal: Generate,
        context: AgentContext,
    ): PartialPlan {
        val trigger = AchievementGoalInvocation(genGoal.value)
        val generationStrategy = getStrategyFromID(planID, context.planLibrary)

        return PlanFactory(
            trigger = trigger,
            goals = listOf(genGoal),
            guard = Truth.TRUE,
            generationStrategy = generationStrategy,
        ).build() as PartialPlan
    }
}
