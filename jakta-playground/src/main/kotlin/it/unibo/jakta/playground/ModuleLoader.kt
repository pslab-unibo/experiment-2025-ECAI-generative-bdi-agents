package it.unibo.jakta.playground

import it.unibo.jakta.agents.bdi.engine.depinjection.JaktaKoin
import it.unibo.jakta.agents.bdi.engine.depinjection.JaktaKoin.engineJsonModule
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializersModuleProvider
import it.unibo.jakta.agents.bdi.generationstrategies.lm.serialization.LMPlanGenJsonModule
import it.unibo.jakta.playground.explorer.serialization.ExplorerJsonModule
import it.unibo.jakta.playground.gridworld.serialization.GridWorldJsonModule
import org.koin.core.qualifier.named
import org.koin.dsl.module

object ModuleLoader {
    val jsonModule =
        module {
            single<SerializersModuleProvider>(named("ExplorerJsonModule")) { ExplorerJsonModule() }
            single<SerializersModuleProvider>(named("GridWorldJsonModule")) { GridWorldJsonModule() }
            single<SerializersModuleProvider>(named("LMPlanGenJsonModule")) { LMPlanGenJsonModule() }
        }

    fun loadModules() = JaktaKoin.loadAdditionalModules(engineJsonModule, jsonModule)
}
