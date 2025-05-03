package it.unibo.jakta.agents.bdi.formatters.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import it.unibo.jakta.agents.bdi.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.termFormatter

class AdmissibleBeliefSerializer : JsonSerializer<AdmissibleBelief>() {
    override fun serialize(
        admissibleBelief: AdmissibleBelief,
        gen: JsonGenerator,
        serializers: SerializerProvider,
    ) {
        gen.writeStartObject()

        gen.writeStringField("rule", termFormatter.format(admissibleBelief.rule))

        admissibleBelief.purpose?.let {
            gen.writeStringField("purpose", it)
        }

        gen.writeEndObject()
    }
}
