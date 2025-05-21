package it.unibo.jakta.agents.bdi.engine.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object FallbackSerializer : KSerializer<Any> {
    override val descriptor =
        buildClassSerialDescriptor("Any") {
            element<String>("value")
        }

    override fun serialize(
        encoder: Encoder,
        value: Any,
    ) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.toString())
        }
    }

    // Return the string, no real deserialization here
    override fun deserialize(decoder: Decoder): Any =
        decoder.decodeStructure(descriptor) {
            decodeStringElement(descriptor, 0)
        }
}
