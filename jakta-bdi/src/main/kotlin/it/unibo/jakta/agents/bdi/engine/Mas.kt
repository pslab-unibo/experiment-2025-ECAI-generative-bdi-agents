package it.unibo.jakta.agents.bdi.engine

import it.unibo.jakta.agents.bdi.engine.actions.effects.EnvironmentChange
import it.unibo.jakta.agents.bdi.engine.environment.Environment
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.engine.generation.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.impl.MasImpl
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.logging.loggers.MasLogger
import org.koin.core.module.Module

interface Mas {
    val masID: MasID

    val environment: Environment

    val agents: Iterable<Agent>

    val executionStrategy: ExecutionStrategy

    val generationStrategy: GenerationStrategy?

    val loggingConfig: LoggingConfig?

    val logger: MasLogger?

    val modules: List<Module>

    fun start()

    fun stop()

    fun applyEnvironmentEffects(effects: Iterable<EnvironmentChange>)

    fun copy(
        masID: MasID = this.masID,
        environment: Environment = this.environment,
        agents: Iterable<Agent> = this.agents,
        executionStrategy: ExecutionStrategy = this.executionStrategy,
        generationStrategy: GenerationStrategy? = this.generationStrategy,
        loggingConfig: LoggingConfig? = this.loggingConfig,
        logger: MasLogger? = this.logger,
    ): Mas =
        MasImpl(
            masID = masID,
            environment = environment,
            agents = agents,
            executionStrategy = executionStrategy,
            generationStrategy = generationStrategy,
            loggingConfig = loggingConfig,
            logger = logger,
        )

    companion object {
        fun of(
            executionStrategy: ExecutionStrategy,
            environment: Environment,
            agent: Agent,
            vararg agents: Agent,
        ): Mas =
            of(
                MasID(),
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
                MasID(),
                executionStrategy,
                environment,
                agents.asIterable() + agent,
                generationStrategy,
                loggingConfig,
            )

        fun of(
            masID: MasID = MasID(),
            executionStrategy: ExecutionStrategy,
            environment: Environment,
            agents: Iterable<Agent>,
            generationStrategy: GenerationStrategy? = null,
            loggingConfig: LoggingConfig? = null,
            modules: List<Module> = emptyList(),
        ): Mas {
            val mas =
                MasImpl(
                    masID,
                    executionStrategy,
                    environment,
                    agents,
                    generationStrategy,
                    loggingConfig,
                    modules = modules,
                )
            return MasInitialization
                .defaultInitializer(mas)
                .initialize()
        }
    }
}
