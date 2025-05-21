package it.unibo.jakta.agents.bdi.dsl

import it.unibo.jakta.agents.bdi.dsl.environment.EnvironmentScope
import it.unibo.jakta.agents.bdi.dsl.logging.LoggingConfigScope
import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.Mas
import it.unibo.jakta.agents.bdi.engine.MasID
import it.unibo.jakta.agents.bdi.engine.environment.Environment
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationStrategy
import org.koin.core.module.Module

@JaktaDSL
class MasScope : ScopeBuilder<Mas> {
    private val masID = MasID()
    var env: Environment = Environment.of()
    var agents: List<Agent> = emptyList()
    var executionStrategy = ExecutionStrategy.oneThreadPerMas()
    var generationStrategy: GenerationStrategy? = null
    var loggingConfig: LoggingConfig? = null
    var modules: List<Module> = emptyList()

    fun environment(f: EnvironmentScope.() -> Unit): MasScope {
        env = EnvironmentScope().also(f).build()
        return this
    }

    fun environment(environment: Environment): MasScope {
        env = environment
        return this
    }

    fun agent(
        name: String,
        f: AgentScope.() -> Unit,
    ): MasScope {
        agents += AgentScope(masID, name).also(f).build()
        return this
    }

    fun executionStrategy(f: () -> ExecutionStrategy): MasScope {
        executionStrategy = f()
        return this
    }

    fun loggingConfig(block: LoggingConfigScope.() -> Unit): MasScope {
        loggingConfig = LoggingConfigScope().also(block).build()
        return this
    }

    override fun build(): Mas =
        Mas.of(
            masID,
            executionStrategy,
            env,
            agents,
            generationStrategy,
            loggingConfig,
            modules,
        )
}
