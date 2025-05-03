package it.unibo.jakta.agents.bdi.formatters.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.goalFormatter
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.termFormatter
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.triggerFormatter
import it.unibo.jakta.agents.bdi.plans.Plan

class PlanSerializer : JsonSerializer<Plan>() {
    override fun serialize(
        plan: Plan,
        gen: JsonGenerator,
        serializers: SerializerProvider,
    ) {
        gen.writeStartObject()

        gen.writeStringField("trigger", triggerFormatter.format(plan.trigger))

        gen.writeStringField("guard", termFormatter.format(plan.guard))

        gen.writeArrayFieldStart("goals")
        plan.goals.forEach { gen.writeString(goalFormatter.format(it)) }
        gen.writeEndArray()

        gen.writeEndObject()
    }
}
