package it.unibo.jakta.agents.bdi.formatters.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.goalFormatter
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.termFormatter
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.triggerFormatter
import it.unibo.jakta.agents.bdi.intentions.Intention

class IntentionSerializer : JsonSerializer<Intention>() {
    override fun serialize(
        intention: Intention,
        gen: JsonGenerator,
        serializers: SerializerProvider,
    ) {
        gen.writeStartObject()

        gen.writeStringField("id", intention.id.id)
        gen.writeBooleanField("isSuspended", intention.isSuspended)

        gen.writeArrayFieldStart("recordStack")
        intention.recordStack.forEach { record ->
            gen.writeStartObject()

            gen.writeObjectFieldStart("planId")
            gen.writeStringField("trigger", triggerFormatter.format(record.plan.trigger))
            gen.writeStringField("guard", termFormatter.format(record.plan.guard))
            gen.writeEndObject()

            gen.writeArrayFieldStart("goalQueue")
            record.goalQueue.forEach { gen.writeString(goalFormatter.format(it)) }
            gen.writeEndArray()

            gen.writeEndObject()
        }
        gen.writeEndArray()

        gen.writeEndObject()
    }
}
