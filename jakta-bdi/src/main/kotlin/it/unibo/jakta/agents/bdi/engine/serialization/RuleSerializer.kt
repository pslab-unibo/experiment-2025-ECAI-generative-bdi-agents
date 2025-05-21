package it.unibo.jakta.agents.bdi.engine.serialization

import it.unibo.jakta.agents.bdi.engine.JaktaParser.tangleRule
import it.unibo.jakta.agents.bdi.engine.formatters.DefaultFormatters.termFormatter
import it.unibo.tuprolog.core.Rule
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive

object RuleSerializer : KSerializer<Rule> {
    override val descriptor =
        buildClassSerialDescriptor("Rule") {
            element<String>("rule")
        }

    override fun serialize(
        encoder: Encoder,
        value: Rule,
    ) {
        require(encoder is JsonEncoder) { "This serializer can only be used with JSON" }
        val jsonPrimitive = JsonPrimitive(termFormatter.format(value))
        encoder.encodeJsonElement(jsonPrimitive)
    }

    override fun deserialize(decoder: Decoder): Rule {
        require(decoder is JsonDecoder) { "This serializer can only be used with JSON" }

        val element = decoder.decodeJsonElement()
        require(element is JsonPrimitive) { "Expected JsonPrimitive for Rule deserialization" }

        val value = element.content
        val rule = tangleRule(value)
        require(rule != null) { "Expected parsable rule" }

        return rule
    }
}
