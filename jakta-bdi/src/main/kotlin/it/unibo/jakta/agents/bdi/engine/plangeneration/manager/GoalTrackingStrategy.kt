package it.unibo.jakta.agents.bdi.engine.plangeneration.manager

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.environment.Environment
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.engine.goals.TrackGoalExecution
import it.unibo.jakta.agents.bdi.engine.intentions.Intention
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plangeneration.manager.impl.GoalTrackingStrategyImpl

interface GoalTrackingStrategy {
    val logger: AgentLogger?
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
            logger: AgentLogger? = null,
        ): GoalTrackingStrategy = GoalTrackingStrategyImpl(invalidationStrategy, logger)
    }
}
