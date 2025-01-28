package it.unibo.jakta.agents.bdi.impl

import it.unibo.jakta.agents.bdi.plans.Plan

interface LLMPlan {
    val plans: Iterable<Plan>
}
