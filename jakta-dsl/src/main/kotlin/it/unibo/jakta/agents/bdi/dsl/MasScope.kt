package it.unibo.jakta.agents.bdi.dsl

import it.unibo.jakta.agents.bdi.Agent
import it.unibo.jakta.agents.bdi.Mas
import it.unibo.jakta.agents.bdi.dsl.environment.EnvironmentScope
import it.unibo.jakta.agents.bdi.dsl.logging.LoggingConfigScope
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy

@JaktaDSL
class MasScope : Builder<Mas> {
    var env: Environment = Environment.of()
    var agents: List<Agent> = emptyList()
    var executionStrategy = ExecutionStrategy.oneThreadPerMas()
    var generationStrategy: GenerationStrategy? = null
    var loggingConfig: LoggingConfig? = null

    fun environment(f: EnvironmentScope.() -> Unit): MasScope {
        env = EnvironmentScope().also(f).build()
        return this
    }

    fun environment(environment: Environment): MasScope {
        env = environment
        return this
    }

    fun agent(name: String, f: AgentScope.() -> Unit): MasScope {
        agents += AgentScope(name).also(f).build()
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

    override fun build(): Mas = Mas.of(
        executionStrategy,
        env,
        agents,
        generationStrategy,
        loggingConfig,
    )
}
