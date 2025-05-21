package it.unibo.jakta.agents.bdi.dsl.actions

import it.unibo.jakta.agents.bdi.engine.Jakta.capitalize
import it.unibo.jakta.agents.bdi.engine.actions.Action

object ActionMetadata {
    class ActionContext(
        val action: Action<*, *, *>,
    ) {
        val functor = action.actionSignature.name
        val args = action.actionSignature.parameterNames.map { "`${it.capitalize()}`" }
    }
}
