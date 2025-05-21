package it.unibo.jakta.agents.bdi.generationstrategies.lm.dsl

import it.unibo.jakta.agents.bdi.engine.actions.ExternalAction
import it.unibo.jakta.agents.bdi.engine.actions.InternalAction
import it.unibo.jakta.agents.bdi.engine.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.engine.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.engine.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.generationstrategies.lm.Remark

data class AgentContextProperties(
    val internalActions: Iterable<InternalAction>,
    val externalActions: Iterable<ExternalAction>,
    val actualBeliefs: BeliefBase,
    val admissibleBeliefs: Iterable<AdmissibleBelief>,
    val admissibleGoals: Iterable<AdmissibleGoal>,
    val actualGoals: Iterable<Plan>,
    val initialGoal: GeneratePlan,
    val remarks: Iterable<Remark>,
)
