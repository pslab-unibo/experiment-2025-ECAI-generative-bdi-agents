package it.unibo.jakta.agents.bdi

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.actions.effects.EnvironmentChange
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.impl.MasImpl
import it.unibo.jakta.agents.bdi.logging.LoggerFactory.createLogger
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plans.generation.GenerationStrategy

interface Mas {
    val environment: Environment
    val agents: Iterable<Agent>
    val executionStrategy: ExecutionStrategy
    val generationStrategy: GenerationStrategy?
    val loggingConfig: LoggingConfig?
    val logger: KLogger?

    fun start()

    fun applyEnvironmentEffects(effects: Iterable<EnvironmentChange>)

    companion object {
        fun of(
            executionStrategy: ExecutionStrategy,
            environment: Environment,
            agent: Agent,
            generationStrategy: GenerationStrategy? = null,
            loggingConfig: LoggingConfig? = null,
            vararg agents: Agent,
        ): Mas =
            of(
                executionStrategy,
                environment,
                agents.asIterable() + agent,
                generationStrategy,
                loggingConfig,
            )

        fun of(
            executionStrategy: ExecutionStrategy,
            environment: Environment,
            agents: Iterable<Agent>,
            generationStrategy: GenerationStrategy? = null,
            loggingConfig: LoggingConfig? = null,
        ): Mas {
            val logger = loggingConfig?.let {
                createLogger(loggingConfig, "mas")
            }
            return MasImpl(
                executionStrategy,
                environment,
                agents,
                generationStrategy,
                loggingConfig,
                logger,
            )
        }
    }
}
