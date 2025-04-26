package it.unibo.jakta.agents.bdi.dsl.actions

import it.unibo.jakta.agents.bdi.Jakta.capitalize
import it.unibo.jakta.agents.bdi.actions.Action

object ActionMetadata {
    class ActionContext(val action: Action<*, *, *>) {
        val functor = action.extendedSignature.name
        val args = action.extendedSignature.parameterNames.map { "`${it.capitalize()}`" }
    }
}
