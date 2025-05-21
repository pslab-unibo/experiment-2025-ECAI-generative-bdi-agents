package it.unibo.jakta.agents.bdi.engine.serialization.modules

import kotlinx.serialization.modules.SerializersModule

interface SerializersModuleProvider {
    val modules: SerializersModule
}
