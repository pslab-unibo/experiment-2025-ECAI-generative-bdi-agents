package it.unibo.jakta.agents.bdi.plangeneration.manager.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.plangeneration.manager.InvalidationStrategy
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.PlanID

class InvalidationStrategyImpl(
    override val logger: KLogger?,
) : InvalidationStrategy {

    override fun reset(
        context: AgentContext,
    ): ExecutionResult {
        return ExecutionResult(
            newAgentContext = AgentContext.of(),
        )
    }

    override fun invalidate(
        failedPlanID: PlanID,
        context: AgentContext,
    ): ExecutionResult {
        val updatedPlanLibrary = context.planLibrary.removePlan(failedPlanID) { it is PartialPlan }
        return ExecutionResult(
            newAgentContext = context.copy(
                planLibrary = updatedPlanLibrary,
            ),
        )
    }
}
