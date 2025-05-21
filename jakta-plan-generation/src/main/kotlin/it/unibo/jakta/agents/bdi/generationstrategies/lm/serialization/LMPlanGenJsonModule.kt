package it.unibo.jakta.agents.bdi.generationstrategies.lm.serialization

import it.unibo.jakta.agents.bdi.engine.logging.events.JaktaLogEvent
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializersModuleProvider
import it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.events.LMGenerationCompleted
import it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.events.LMGenerationRequested
import it.unibo.jakta.agents.bdi.generationstrategies.lm.strategy.LMGenerationStrategy
import it.unibo.jakta.agents.bdi.generationstrategies.lm.strategy.impl.LMGenerationStrategyImpl
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.core.annotation.Single

@Single
class LMPlanGenJsonModule : SerializersModuleProvider {
    override val modules =
        SerializersModule {
            polymorphic(JaktaLogEvent::class) {
                subclass(LMGenerationCompleted::class)
                subclass(LMGenerationRequested::class)
            }

            polymorphic(LMGenerationStrategy::class) {
                subclass(LMGenerationStrategyImpl::class)
            }
        }
}
