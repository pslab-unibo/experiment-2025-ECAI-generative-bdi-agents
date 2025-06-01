package it.unibo.jakta.agents.bdi.dsl.plans

object PlanMetadata {
    class PlanContext(
        val plan: PlanScope,
    ) {
        val trigger = plan.trigger
    }

    infix fun PlanScope.meaning(block: PlanContext.() -> String): PlanScope {
        val context = PlanContext(this)
        val purpose = context.block()
        this.trigger = trigger.copy(purpose = purpose)
        return this
    }
}
