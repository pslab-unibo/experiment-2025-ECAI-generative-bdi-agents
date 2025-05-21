package it.unibo.jakta.agents.bdi.engine.serialization

import it.unibo.tuprolog.solve.Signature
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object SignatureSerializer : KSerializer<Signature> {
    override val descriptor =
        buildClassSerialDescriptor("Rule") {
            element<String>("name")
            element<String>("arity")
            element<String>("vararg")
        }

    @Serializable
    @SerialName("Signature")
    private data class SignatureSurrogate(
        val name: String,
        val arity: Int,
        val vararg: Boolean,
    )

    override fun serialize(
        encoder: Encoder,
        value: Signature,
    ) {
        val surrogate =
            SignatureSurrogate(
                name = value.name,
                arity = value.arity,
                vararg = value.vararg,
            )
        encoder.encodeSerializableValue(SignatureSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Signature {
        val surrogate = decoder.decodeSerializableValue(SignatureSurrogate.serializer())
        return Signature(surrogate.name, surrogate.arity, surrogate.vararg)
    }
}
