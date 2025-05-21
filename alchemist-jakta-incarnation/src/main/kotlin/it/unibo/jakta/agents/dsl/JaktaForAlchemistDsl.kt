package it.unibo.jakta.agents.dsl

import it.unibo.alchemist.jakta.properties.JaktaEnvironmentForAlchemist
import it.unibo.alchemist.model.Position
import it.unibo.jakta.agents.bdi.dsl.AgentScope
import it.unibo.jakta.agents.bdi.dsl.JaktaDSL
import it.unibo.jakta.agents.bdi.dsl.ScopeBuilder
import it.unibo.jakta.agents.bdi.dsl.actions.ExternalActionsScope
import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.MasID
import it.unibo.jakta.agents.bdi.engine.actions.ExternalAction
import it.unibo.jakta.agents.bdi.engine.environment.Environment

class WrappedAgent(
    val agent: Agent,
    val actions: Map<String, ExternalAction>,
)

class JaktaForAlchemistMasScope : ScopeBuilder<WrappedAgent> {
    private val masID = MasID()
    lateinit var agents: Agent
    var actions: Map<String, ExternalAction> = emptyMap()

    fun environment(f: JaktaForAlchemistEnvironmentScope.() -> Unit): JaktaForAlchemistMasScope {
        actions += JaktaForAlchemistEnvironmentScope().also(f).build()
        return this
    }

    fun environment(e: Environment): JaktaForAlchemistMasScope {
        actions += e.externalActions
        return this
    }

    fun agent(
        name: String,
        f: AgentScope.() -> Unit,
    ): JaktaForAlchemistMasScope {
        agents = AgentScope(masID, name).also(f).build()
        return this
    }

    override fun build(): WrappedAgent = WrappedAgent(agents, actions)
}

class JaktaForAlchemistEnvironmentScope : ScopeBuilder<Map<String, ExternalAction>> {
    private val actionsScopes by lazy { ExternalActionsScope() }

    infix fun actions(actions: ExternalActionsScope.() -> Unit): JaktaForAlchemistEnvironmentScope {
        actionsScopes.also(actions)
        return this
    }

    override fun build(): Map<String, ExternalAction> = actionsScopes.build()
}

// TODO("This DSL entrypoint can create more than one agent, the simulation must know how to handle it.")
@JaktaDSL
fun <P : Position<P>> JaktaEnvironmentForAlchemist<P>.device(f: JaktaForAlchemistMasScope.() -> Unit): Agent {
    val wrappedAgent = JaktaForAlchemistMasScope().also(f).build()
    this.externalActions += wrappedAgent.actions
    return wrappedAgent.agent
}
