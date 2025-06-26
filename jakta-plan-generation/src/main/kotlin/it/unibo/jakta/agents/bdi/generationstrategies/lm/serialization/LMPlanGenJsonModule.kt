package it.unibo.jakta.agents.bdi.generationstrategies.lm.serialization

import it.unibo.jakta.agents.bdi.engine.logging.events.LogEvent
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializersModuleProvider
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationConfig
import it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.events.LMGenerationRequested
import it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.events.LMMessageReceived
import it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.events.LMMessageSent
import it.unibo.jakta.agents.bdi.generationstrategies.lm.strategy.LMGenerationStrategy
import it.unibo.jakta.agents.bdi.generationstrategies.lm.strategy.impl.LMGenerationStrategyImpl
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

class LMPlanGenJsonModule : SerializersModuleProvider {
    override val modules =
        SerializersModule {
            polymorphic(LogEvent::class) {
                subclass(LMMessageSent::class)
                subclass(LMMessageReceived::class)
                subclass(LMGenerationRequested::class)
            }

            polymorphic(LMGenerationStrategy::class) {
                subclass(LMGenerationStrategyImpl::class)
            }

            polymorphic(LMGenerationConfig::class) {
                subclass(LMGenerationConfig.LMGenerationConfigContainer::class)
                subclass(LMGenerationConfig.LMGenerationConfigUpdate::class)
            }
        }
}
