package it.unibo.jakta.agents.bdi.engine.serialization

import it.unibo.jakta.agents.bdi.engine.JaktaParser.tangleStruct
import it.unibo.jakta.agents.bdi.engine.formatters.DefaultFormatters.termFormatter
import it.unibo.tuprolog.core.Struct
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive

object StructSerializer : KSerializer<Struct> {
    override val descriptor =
        buildClassSerialDescriptor("Struct") {
            element<String>("value")
        }

    override fun serialize(
        encoder: Encoder,
        value: Struct,
    ) {
        require(encoder is JsonEncoder) { "This serializer can only be used with JSON" }
        val jsonPrimitive = JsonPrimitive(termFormatter.format(value))
        encoder.encodeJsonElement(jsonPrimitive)
    }

    override fun deserialize(decoder: Decoder): Struct {
        require(decoder is JsonDecoder) { "This serializer can only be used with JSON" }

        val element = decoder.decodeJsonElement()
        require(element is JsonPrimitive) { "Expected JsonPrimitive for Struct deserialization" }

        val valueStr = element.content
        val struct = tangleStruct(valueStr)
        require(struct != null) { "Expected parsable value" }

        return struct
    }
}
