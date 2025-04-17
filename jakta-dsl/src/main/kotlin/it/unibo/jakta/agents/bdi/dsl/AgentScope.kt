package it.unibo.jakta.agents.bdi.dsl

import it.unibo.jakta.agents.bdi.Agent
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.actions.InternalActions
import it.unibo.jakta.agents.bdi.dsl.actions.InternalActionsScope
import it.unibo.jakta.agents.bdi.dsl.beliefs.BeliefsScope
import it.unibo.jakta.agents.bdi.dsl.goals.InitialGoalsScope
import it.unibo.jakta.agents.bdi.dsl.plans.PlansScope
import it.unibo.jakta.agents.bdi.dsl.templates.TemplatesScope
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.executionstrategies.TimeDistribution
import it.unibo.jakta.agents.bdi.executionstrategies.setTimeDistribution
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanLibrary

class AgentScope(
    val name: String? = null,
    private val externalActions: Map<String, ExternalAction> = emptyMap(),
) : Builder<Agent> {
    private val actionsScope by lazy { InternalActionsScope() }
    private val internalActions by lazy { InternalActions.default() + actionsScope.build() }

    private val templatesScope by lazy { TemplatesScope() }
    private val templates by lazy { templatesScope.build() }

    private val beliefsScope by lazy { BeliefsScope(templates) }
    private val goalsScope by lazy { InitialGoalsScope(templates) }

    private val actionTemplates by lazy {
        val externalActionsTemplates = externalActions.mapNotNull { it.value.signature.template }
        val internalActionsTemplates = internalActions.mapNotNull { it.value.signature.template }
        externalActionsTemplates + internalActionsTemplates
    }

    private val plansScope by lazy { PlansScope(templates + actionTemplates) }

    private var plans = emptyList<Plan>()
    private lateinit var time: TimeDistribution
    var generationStrategy: GenerationStrategy? = null

    fun templates(f: TemplatesScope.() -> Unit): AgentScope {
        templatesScope.also(f)
        return this
    }

    fun beliefs(f: BeliefsScope.() -> Unit): AgentScope {
        beliefsScope.also(f)
        return this
    }

    fun goals(f: InitialGoalsScope.() -> Unit): AgentScope {
        goalsScope.also(f)
        return this
    }

    fun plans(f: PlansScope.() -> Unit): AgentScope {
        plansScope.also(f)
        return this
    }

    fun plans(plansList: Iterable<Plan>): AgentScope {
        plans = plans + plansList
        return this
    }

    fun actions(f: InternalActionsScope.() -> Unit): AgentScope {
        actionsScope.also(f)
        return this
    }

    fun timeDistribution(timeDistribution: TimeDistribution): AgentScope {
        this.time = timeDistribution
        return this
    }

    override fun build(): Agent {
        val builtPlans = plansScope.build()
        val plans = builtPlans.first
        val planTemplates = builtPlans.second
        var agent = Agent.of(
            name = name.orEmpty(),
            beliefBase = beliefsScope.build(),
            generationStrategy = generationStrategy,
            events = goalsScope.build().map { Event.of(it) },
            planLibrary = PlanLibrary.of(this.plans + plans),
            internalActions = internalActions,
            templates = templates + actionTemplates + planTemplates,
        )
        if (this::time.isInitialized) {
            agent = agent.setTimeDistribution(time)
        }
        return agent
    }
}
