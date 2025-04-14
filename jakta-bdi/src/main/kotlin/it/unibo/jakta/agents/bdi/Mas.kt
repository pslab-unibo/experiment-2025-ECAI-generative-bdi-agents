package it.unibo.jakta.agents.bdi

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.actions.effects.EnvironmentChange
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.impl.MasImpl
import it.unibo.jakta.agents.bdi.logging.LoggerFactory.createLogger
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy

interface Mas {
    val environment: Environment
    val agents: Iterable<Agent>
    val executionStrategy: ExecutionStrategy
    val generationStrategy: GenerationStrategy?
    val loggingConfig: LoggingConfig?
    val logger: KLogger?

    fun start()

    fun applyEnvironmentEffects(effects: Iterable<EnvironmentChange>)

    fun copy(
        environment: Environment = this.environment,
        agents: Iterable<Agent> = this.agents,
        executionStrategy: ExecutionStrategy = this.executionStrategy,
        generationStrategy: GenerationStrategy? = this.generationStrategy,
        loggingConfig: LoggingConfig? = this.loggingConfig,
        logger: KLogger? = this.logger,
    ): Mas {
        return MasImpl(
            environment = environment,
            agents = agents,
            executionStrategy = executionStrategy,
            generationStrategy = generationStrategy,
            loggingConfig = loggingConfig,
            logger = logger,
        )
    }

    companion object {
        fun of(
            executionStrategy: ExecutionStrategy,
            environment: Environment,
            agent: Agent,
            vararg agents: Agent,
        ): Mas =
            of(
                executionStrategy,
                environment,
                agents.asIterable() + agent,
            )

        fun of(
            executionStrategy: ExecutionStrategy,
            environment: Environment,
            generationStrategy: GenerationStrategy,
            loggingConfig: LoggingConfig,
            agent: Agent,
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
            val mas = MasImpl(
                executionStrategy,
                environment,
                agents,
                generationStrategy,
                loggingConfig,
                logger,
            )
            return MasInitialization
                .defaultInitializer(mas)
                .initialize()
        }
    }
}
