package it.unibo.jakta.agents.bdi.engine.serialization

import it.unibo.tuprolog.core.Var
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive

object VarSerializer : KSerializer<Var> {
    override val descriptor =
        buildClassSerialDescriptor("Var") {
            element<String>("value")
        }

    override fun serialize(
        encoder: Encoder,
        value: Var,
    ) {
        require(encoder is JsonEncoder) { "This serializer can only be used with JSON" }
        val jsonPrimitive = JsonPrimitive(value.name) // not the completeName
        encoder.encodeJsonElement(jsonPrimitive)
    }

    override fun deserialize(decoder: Decoder): Var {
        require(decoder is JsonDecoder) { "This serializer can only be used with JSON" }

        val element = decoder.decodeJsonElement()
        require(element is JsonPrimitive) { "Expected JsonPrimitive for Var deserialization" }

        val valueVar = element.content
        val `var` = Var.of(valueVar)

        return `var`
    }
}
