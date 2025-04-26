package it.unibo.jakta.generationstrategies.lm.pipeline.filtering

import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.plangeneration.manager.impl.GenerationPlanBuilder
import it.unibo.jakta.agents.bdi.plangeneration.manager.impl.GenerationPlanBuilder.createNewTriggerFromGoal
import it.unibo.jakta.agents.bdi.plangeneration.manager.impl.GenerationPlanBuilder.getFailureTrigger
import it.unibo.jakta.agents.bdi.plans.PlanLibrary

class ContextFilterImpl : ContextFilter {
    override fun filter(
        initialGoal: GeneratePlan,
        context: AgentContext,
        externalActions: List<ExternalAction>,
    ): AgentContext {
        /*
         * Do not show to the LM the special plan that makes the generation start when a plan is not available.
         */
        val trigger = createNewTriggerFromGoal(initialGoal.goal)?.let { getFailureTrigger(it) }
        return if (trigger != null) {
            val id = GenerationPlanBuilder.getGenerationPlanID(trigger)
            context.copy(
                planLibrary = PlanLibrary.of(
                    context.planLibrary.plans.filterNot {
                        it.id == id
                    },
                ),
            )
        } else {
            context
        }
    }
}
