package it.unibo.jakta.agents.bdi.engine

import it.unibo.jakta.agents.bdi.engine.actions.InternalAction
import it.unibo.jakta.agents.bdi.engine.actions.InternalActions
import it.unibo.jakta.agents.bdi.engine.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.engine.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.engine.events.Event
import it.unibo.jakta.agents.bdi.engine.events.EventQueue
import it.unibo.jakta.agents.bdi.engine.impl.AgentImpl
import it.unibo.jakta.agents.bdi.engine.intentions.IntentionPool
import it.unibo.jakta.agents.bdi.engine.intentions.SchedulingResult
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.plangeneration.registry.GenerationProcessRegistry
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.engine.plans.PlanLibrary
import it.unibo.tuprolog.utils.Taggable
import java.util.UUID

interface Agent : Taggable<Agent> {
    val masID: MasID?

    val agentID: AgentID

    val name: String

    val generationStrategy: GenerationStrategy?

    val loggingConfig: LoggingConfig?

    val logger: AgentLogger?

    /** Snapshot of Agent's Actual State */
    val context: AgentContext

    /** Event Selection Function*/
    fun selectEvent(events: EventQueue): Event?

    /** Plan Selection Function */
    fun selectApplicablePlan(plans: Iterable<Plan>): Plan?

    /** Intention Selection Function */
    fun scheduleIntention(intentions: IntentionPool): SchedulingResult

    fun copy(agentContext: AgentContext = this.context) =
        of(
            this.masID,
            this.agentID,
            this.name,
            this.generationStrategy,
            this.loggingConfig,
            this.logger,
            agentContext.copy(),
        )

    fun copy(
        masID: MasID? = this.masID,
        generationStrategy: GenerationStrategy? = this.generationStrategy,
        loggingConfig: LoggingConfig? = this.loggingConfig,
        logger: AgentLogger? = this.logger,
        beliefBase: BeliefBase = this.context.beliefBase,
        events: EventQueue = this.context.events,
        planLibrary: PlanLibrary = this.context.planLibrary,
        internalActions: Map<String, InternalAction> = this.context.internalActions,
        generationProcesses: GenerationProcessRegistry = this.context.generationProcesses,
        intentions: IntentionPool = this.context.intentions,
        admissibleGoals: Set<AdmissibleGoal> = this.context.admissibleGoals,
        admissibleBeliefs: Set<AdmissibleBelief> = this.context.admissibleBeliefs,
    ) = of(
        masID,
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
        fun empty(masID: MasID? = null): Agent = AgentImpl(masID, AgentContext.of())

        // CPD-OFF
        fun of(
            masID: MasID? = null,
            agentID: AgentID = AgentID(),
            name: String = "Agent-" + UUID.randomUUID(),
            generationStrategy: GenerationStrategy? = null,
            loggingConfig: LoggingConfig? = null,
            logger: AgentLogger? = null,
            beliefBase: BeliefBase = BeliefBase.empty(),
            events: EventQueue = emptyList(),
            planLibrary: PlanLibrary = PlanLibrary.empty(),
            internalActions: Map<String, InternalAction> = InternalActions.default(),
            generationProcesses: GenerationProcessRegistry = GenerationProcessRegistry.empty(),
            intentions: IntentionPool = IntentionPool.empty(),
            admissibleGoals: Set<AdmissibleGoal> = emptySet(),
            admissibleBeliefs: Set<AdmissibleBelief> = emptySet(),
        ): Agent =
            AgentImpl(
                masID,
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
        // CPD-ON

        fun of(
            masID: MasID? = null,
            agentID: AgentID = AgentID(),
            name: String = "Agent-" + UUID.randomUUID(),
            generationStrategy: GenerationStrategy? = null,
            loggingConfig: LoggingConfig? = null,
            logger: AgentLogger? = null,
            agentContext: AgentContext,
        ): Agent =
            AgentImpl(
                masID,
                agentContext,
                agentID,
                name,
                generationStrategy,
                loggingConfig,
                logger,
            )
    }
}
