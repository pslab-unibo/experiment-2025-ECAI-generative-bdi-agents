package it.unibo.jakta.agents.bdi.plangeneration.manager

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.goals.TrackGoalExecution
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.plangeneration.manager.impl.GoalTrackingStrategyImpl

interface GoalTrackingStrategy {
    val logger: KLogger?
    val invalidationStrategy: InvalidationStrategy

    fun trackGoalExecution(
        genGoal: TrackGoalExecution,
        intention: Intention,
        context: AgentContext,
        environment: Environment,
        runIntention: (Intention, AgentContext, Environment) -> ExecutionResult,
    ): ExecutionResult

    companion object {
        fun of(
            invalidationStrategy: InvalidationStrategy,
            logger: KLogger? = null,
        ) = GoalTrackingStrategyImpl(invalidationStrategy, logger)
    }
}
