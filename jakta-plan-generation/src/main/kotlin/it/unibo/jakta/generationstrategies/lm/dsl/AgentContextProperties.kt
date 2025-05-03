package it.unibo.jakta.generationstrategies.lm.dsl

import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.actions.InternalAction
import it.unibo.jakta.agents.bdi.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.generationstrategies.lm.Remark

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
