package it.unibo.jakta.agents.bdi.dsl

import it.unibo.jakta.agents.bdi.Agent
import it.unibo.jakta.agents.bdi.Jakta.implementation
import it.unibo.jakta.agents.bdi.Mas
import it.unibo.jakta.agents.bdi.actions.effects.BeliefChange
import it.unibo.jakta.agents.bdi.actions.effects.EventChange
import it.unibo.jakta.agents.bdi.actions.effects.PlanChange
import it.unibo.jakta.agents.bdi.context.ContextUpdate.ADDITION
import it.unibo.jakta.agents.bdi.dsl.environment.EnvironmentScope
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.logging.ActionAddition
import kotlin.collections.forEach

@JaktaDSL
class MasScope : Builder<Mas> {
    var env: Environment = Environment.of()
    var agents: List<Agent> = emptyList()
    var executionStrategy = ExecutionStrategy.oneThreadPerMas()

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

    override fun build(): Mas = Mas.of(executionStrategy, env, agents).also {
        env.externalActions.values.forEach { act -> it.logger.implementation(ActionAddition(act)) }
        agents.forEach { agt ->
            agt.context.events.forEach { agt.logger.implementation(EventChange(it, ADDITION)) }
            agt.context.planLibrary.plans.forEach { agt.logger.implementation(PlanChange(it, ADDITION)) }
            agt.context.beliefBase.forEach { agt.logger.implementation(BeliefChange(it, ADDITION)) }
            agt.context.internalActions.values.forEach { agt.logger.implementation(ActionAddition(it)) }
        }
    }
}
