package it.unibo.jakta.agents.bdi.dsl

import it.unibo.jakta.agents.bdi.dsl.actions.InternalActionsScope
import it.unibo.jakta.agents.bdi.dsl.beliefs.BeliefsScope
import it.unibo.jakta.agents.bdi.dsl.goals.InitialGoalsScope
import it.unibo.jakta.agents.bdi.dsl.plans.PlansScope
import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.MasID
import it.unibo.jakta.agents.bdi.engine.actions.InternalActions
import it.unibo.jakta.agents.bdi.engine.events.Event
import it.unibo.jakta.agents.bdi.engine.executionstrategies.TimeDistribution
import it.unibo.jakta.agents.bdi.engine.executionstrategies.setTimeDistribution
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.engine.plans.PlanLibrary

class AgentScope(
    val masID: MasID,
    val name: String? = null,
) : ScopeBuilder<Agent> {
    private val actionsScope by lazy { InternalActionsScope() }
    private val internalActions by lazy { InternalActions.default() + actionsScope.build() }

    private val beliefsScope by lazy { BeliefsScope() }
    private val goalsScope by lazy { InitialGoalsScope() }

    private val plansScope by lazy { PlansScope() }

    private var plans = emptyList<Plan>()
    private lateinit var time: TimeDistribution

    var generationStrategy: GenerationStrategy? = null

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
        val beliefs = beliefsScope.build()
        val initialGoals = goalsScope.build()
        val planLibrary = PlanLibrary.of(this.plans + plansScope.build())
        val (goals, admissibleGoals) = initialGoals

        var agent =
            Agent.of(
                masID = masID,
                name = name.orEmpty(),
                beliefBase = beliefs.first,
                generationStrategy = generationStrategy,
                events = goals.map { Event.of(it) },
                planLibrary = planLibrary,
                internalActions = internalActions,
                admissibleGoals = admissibleGoals,
                admissibleBeliefs = beliefs.second,
            )
        if (this::time.isInitialized) {
            agent = agent.setTimeDistribution(time)
        }
        return agent
    }
}
