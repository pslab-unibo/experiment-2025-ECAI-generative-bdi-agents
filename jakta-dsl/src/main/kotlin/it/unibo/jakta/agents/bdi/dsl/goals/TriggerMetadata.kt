package it.unibo.jakta.agents.bdi.dsl.goals

import it.unibo.jakta.agents.bdi.Jakta.capitalize
import it.unibo.jakta.agents.bdi.Jakta.termFormatter
import it.unibo.jakta.agents.bdi.events.Trigger

object TriggerMetadata {
    class TriggerContext(val trigger: Trigger) {
        val functor = trigger.value.functor
        val args = trigger.value.args.map { "`${termFormatter.format(it).capitalize()}`" }
    }

    fun Trigger.meaning(block: TriggerContext.() -> String): Trigger {
        val context = TriggerContext(this)
        val purpose = context.block()
        return this.copy(purpose = purpose)
    }
}
