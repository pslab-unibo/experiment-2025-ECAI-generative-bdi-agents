package it.unibo.jakta.agents.bdi.engine

import it.unibo.jakta.agents.bdi.engine.impl.MasInitializationImpl

interface MasInitialization {
    val mas: Mas

    fun initialize(): Mas

    companion object {
        fun defaultInitializer(mas: Mas): MasInitialization = MasInitializationImpl(mas)
    }
}
