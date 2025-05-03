package it.unibo.jakta.agents.bdi.formatters.serializers

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import it.unibo.jakta.agents.bdi.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.executionstrategies.feedback.PlanApplicabilityResult
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.plans.Plan
import net.logstash.logback.decorate.JsonFactoryDecorator

class JaktaJsonFactoryDecorator : JsonFactoryDecorator {
    override fun decorate(factory: JsonFactory): JsonFactory {
        val objectMapper = (factory.codec as? ObjectMapper) ?: ObjectMapper()

        val jaktaModule = SimpleModule().apply {
            addSerializer(Plan::class.java, PlanSerializer())
            addSerializer(PlanApplicabilityResult::class.java, PlanApplicabilityResultSerializer())
            addSerializer(Intention::class.java, IntentionSerializer())
            addSerializer(AdmissibleGoal::class.java, AdmissibleGoalSerializer())
            addSerializer(AdmissibleBelief::class.java, AdmissibleBeliefSerializer())
        }
        objectMapper.registerModule(jaktaModule)

        factory.codec = objectMapper

        return factory
    }
}
