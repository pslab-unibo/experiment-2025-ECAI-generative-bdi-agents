package it.unibo.jakta.agents.bdi.dsl.environment

import it.unibo.jakta.agents.bdi.dsl.ScopeBuilder
import it.unibo.jakta.agents.bdi.dsl.actions.ExternalActionsScope
import it.unibo.jakta.agents.bdi.engine.environment.Environment

class EnvironmentScope : ScopeBuilder<Environment> {
    private val actionsScopes by lazy { ExternalActionsScope() }
    private var environment = Environment.of()

    infix fun actions(actions: ExternalActionsScope.() -> Unit) {
        actionsScopes.also(actions)
    }

    fun from(environment: Environment) {
        this.environment = environment
    }

    override fun build(): Environment =
        environment.copy(
            externalActions = actionsScopes.build(),
        )
}
