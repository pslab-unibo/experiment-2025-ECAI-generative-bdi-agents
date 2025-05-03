package it.unibo.jakta.agents.bdi.formatters.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import it.unibo.jakta.agents.bdi.executionstrategies.feedback.PlanApplicabilityResult
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.termFormatter
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.triggerFormatter

class PlanApplicabilityResultSerializer : JsonSerializer<PlanApplicabilityResult>() {
    override fun serialize(
        result: PlanApplicabilityResult,
        gen: JsonGenerator,
        serializers: SerializerProvider,
    ) {
        gen.writeStartObject()

        result.trigger?.let {
            gen.writeStringField("trigger", triggerFormatter.format(it))
        }

        result.guards?.let {
            gen.writeObjectFieldStart("guards")
            it.forEach { (struct, evaluation) ->
                val formattedGuard = termFormatter.format(struct)
                gen.writeStringField("guard", formattedGuard)
                gen.writeBooleanField("evaluation", evaluation)
            }
            gen.writeEndObject()
        }

        result.error?.let {
            gen.writeStringField("error", it)
        }

        gen.writeEndObject()
    }
}
