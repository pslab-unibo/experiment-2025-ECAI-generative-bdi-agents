package it.unibo.jakta.agents.bdi.engine.generation.manager.impl

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.engine.generation.manager.InvalidationStrategy
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plans.PartialPlan
import it.unibo.jakta.agents.bdi.engine.plans.PlanID

internal class InvalidationStrategyImpl(
    override val logger: AgentLogger?,
) : InvalidationStrategy {
    override fun invalidate(
        failedPlanID: PlanID,
        context: AgentContext,
    ): ExecutionResult {
        val updatedPlanLibrary = context.planLibrary.removePlan(failedPlanID) { it is PartialPlan }
        return ExecutionResult(
            newAgentContext =
                context.copy(
                    planLibrary = updatedPlanLibrary,
                ),
        )
    }
}
