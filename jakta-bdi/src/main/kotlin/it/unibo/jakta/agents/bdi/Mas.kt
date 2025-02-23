package it.unibo.jakta.agents.bdi

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import it.unibo.jakta.agents.bdi.actions.effects.EnvironmentChange
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.impl.MasImpl
import it.unibo.jakta.agents.bdi.logging.LoggingStrategy
import it.unibo.jakta.agents.bdi.plans.generation.GenerationStrategy

interface Mas {
    val environment: Environment
    val agents: Iterable<Agent>
    val executionStrategy: ExecutionStrategy
    val generationStrategy: GenerationStrategy?
    val loggingStrategy: LoggingStrategy?
    val logger: KLogger get() = logger("[MAS]")

    fun start()

    fun applyEnvironmentEffects(effects: Iterable<EnvironmentChange>)

    companion object {
        fun of(
            executionStrategy: ExecutionStrategy,
            environment: Environment,
            agent: Agent,
            generationStrategy: GenerationStrategy? = null,
            loggingStrategy: LoggingStrategy? = null,
            vararg agents: Agent,
        ): Mas =
            of(
                executionStrategy,
                environment,
                agents.asIterable() + agent,
                generationStrategy,
                loggingStrategy,
            )

        fun of(
            executionStrategy: ExecutionStrategy,
            environment: Environment,
            agents: Iterable<Agent>,
            generationStrategy: GenerationStrategy? = null,
            loggingStrategy: LoggingStrategy? = null,
        ): Mas =
            MasImpl(
                executionStrategy,
                environment,
                agents,
                generationStrategy,
                loggingStrategy,
            )
    }
}
