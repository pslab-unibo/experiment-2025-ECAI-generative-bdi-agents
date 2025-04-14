package it.unibo.jakta.agents.bdi.plangeneration

object InfiniteRecursionGuardConfig {
    const val MAX_CONCURRENT_GENERATION_REQUESTS = 10
    const val MAX_PARTIAL_PLANS_AMOUNT = 100
    const val MAX_GOALS_PER_PARTIAL_PLANS_AMOUNT = 500
    const val MAX_ACHIEVED_GOALS_AMOUNT = 1000
}
