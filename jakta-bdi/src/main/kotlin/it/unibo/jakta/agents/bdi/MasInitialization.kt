package it.unibo.jakta.agents.bdi

import it.unibo.jakta.agents.bdi.impl.MasInitializationImpl

interface MasInitialization {
    val mas: Mas

    fun initialize(): Mas

    companion object {
        fun defaultInitializer(mas: Mas): MasInitialization = MasInitializationImpl(mas)
    }
}
