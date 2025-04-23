package it.unibo.jakta.agents.bdi

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.actions.InternalAction
import it.unibo.jakta.agents.bdi.actions.InternalActions
import it.unibo.jakta.agents.bdi.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.EventQueue
import it.unibo.jakta.agents.bdi.impl.AgentImpl
import it.unibo.jakta.agents.bdi.intentions.IntentionPool
import it.unibo.jakta.agents.bdi.intentions.SchedulingResult
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.registry.GenerationProcessRegistry
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.tuprolog.utils.Taggable
import java.util.*

interface Agent : Taggable<Agent> {

    val agentID: AgentID

    val name: String

    val generationStrategy: GenerationStrategy?

    val loggingConfig: LoggingConfig?

    val logger: KLogger?

    /** Snapshot of Agent's Actual State */
    val context: AgentContext

    /** Event Selection Function*/
    fun selectEvent(events: EventQueue): Event?

    /** Plan Selection Function */
    fun selectApplicablePlan(plans: Iterable<Plan>): Plan?

    /** Intention Selection Function */
    fun scheduleIntention(intentions: IntentionPool): SchedulingResult

    fun copy(agentContext: AgentContext = this.context) = of(
        this.agentID,
        this.name,
        this.generationStrategy,
        this.loggingConfig,
        this.logger,
        agentContext.copy(),
    )

    fun copy(
        generationStrategy: GenerationStrategy? = this.generationStrategy,
        loggingConfig: LoggingConfig? = this.loggingConfig,
        logger: KLogger? = this.logger,
        beliefBase: BeliefBase = this.context.beliefBase,
        events: EventQueue = this.context.events,
        planLibrary: PlanLibrary = this.context.planLibrary,
        internalActions: Map<String, InternalAction> = this.context.internalActions,
        generationProcesses: GenerationProcessRegistry = this.context.generationProcesses,
        intentions: IntentionPool = this.context.intentions,
        admissibleGoals: Set<AdmissibleGoal> = this.context.admissibleGoals,
        admissibleBeliefs: Set<AdmissibleBelief> = this.context.admissibleBeliefs,
    ) = of(
        this.agentID,
        this.name,
        generationStrategy,
        loggingConfig,
        logger,
        context.copy(
            beliefBase,
            events,
            planLibrary,
            internalActions,
            generationProcesses,
            intentions,
            admissibleGoals,
            admissibleBeliefs,
        ),
    )

    companion object {
        fun empty(): Agent = AgentImpl(AgentContext.of())

        fun of(
            agentID: AgentID = AgentID(),
            name: String = "Agent-" + UUID.randomUUID(),
            generationStrategy: GenerationStrategy? = null,
            loggingConfig: LoggingConfig? = null,
            logger: KLogger? = null,
            beliefBase: BeliefBase = BeliefBase.empty(),
            events: EventQueue = emptyList(),
            planLibrary: PlanLibrary = PlanLibrary.empty(),
            internalActions: Map<String, InternalAction> = InternalActions.default(),
            generationProcesses: GenerationProcessRegistry = GenerationProcessRegistry.empty(),
            intentions: IntentionPool = IntentionPool.empty(),
            admissibleGoals: Set<AdmissibleGoal> = emptySet(),
            admissibleBeliefs: Set<AdmissibleBelief> = emptySet(),
        ): Agent = AgentImpl(
            AgentContext.of(
                beliefBase,
                events,
                planLibrary,
                internalActions,
                generationProcesses,
                intentions,
                admissibleGoals,
                admissibleBeliefs,
            ),
            agentID,
            name,
            generationStrategy,
            loggingConfig,
            logger,
        )

        fun of(
            agentID: AgentID = AgentID(),
            name: String = "Agent-" + UUID.randomUUID(),
            generationStrategy: GenerationStrategy? = null,
            loggingConfig: LoggingConfig? = null,
            logger: KLogger? = null,
            agentContext: AgentContext,
        ): Agent = AgentImpl(
            agentContext,
            agentID,
            name,
            generationStrategy,
            loggingConfig,
            logger,
        )
    }
}
