package it.unibo.jakta.agents.bdi.actions.effects

sealed interface SideEffect {
    val name: String get() = this.javaClass.simpleName
    val description: String
}
