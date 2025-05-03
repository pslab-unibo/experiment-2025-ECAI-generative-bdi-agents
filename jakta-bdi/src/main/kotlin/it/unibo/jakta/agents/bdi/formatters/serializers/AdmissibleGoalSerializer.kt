package it.unibo.jakta.agents.bdi.formatters.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import it.unibo.jakta.agents.bdi.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.triggerFormatter

class AdmissibleGoalSerializer : JsonSerializer<AdmissibleGoal>() {
    override fun serialize(
        admissibleGoal: AdmissibleGoal,
        gen: JsonGenerator,
        serializers: SerializerProvider,
    ) {
        gen.writeStartObject()

        gen.writeStringField("trigger", triggerFormatter.format(admissibleGoal.trigger))

        admissibleGoal.trigger.purpose?.let {
            gen.writeStringField("purpose", it)
        }

        gen.writeEndObject()
    }
}
