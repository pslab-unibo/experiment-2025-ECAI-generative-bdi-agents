package it.unibo.jakta.agents.bdi.serializers

import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.serializers.SerializationTestUtils.compareBeliefs
import it.unibo.jakta.agents.bdi.serializers.SerializationTestUtils.modules
import it.unibo.jakta.agents.bdi.serializers.SerializationTestUtils.shouldBeWithTrimming
import it.unibo.tuprolog.core.Struct
import kotlinx.serialization.json.Json
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.getValue

class BeliefSerializationTest :
    FunSpec(),
    KoinTest {
    override fun extensions() = listOf(KoinExtension(modules))

    val json by inject<Json>()

    val testBeliefValue = "customer(source(Source), john, member) :- true"
    val testPurposeValue = "from customer database"
    val testBeliefContent = Struct.of("customer", Struct.of("john"), Struct.of("member"))
    val testBeliefWithPurpose = Belief.wrap(testBeliefContent, purpose = testPurposeValue)
    val testBeliefWithoutPurpose = Belief.wrap(testBeliefContent)

    infix fun Belief.shouldBe(expected: Belief) = compareBeliefs(this, expected) shouldBe true

    init {
        context("A belief") {
            test("with purpose should serialize correctly") {
                val serialized = json.encodeToString(testBeliefWithPurpose)
                val expectedJson = """
                {
                    "type": "Belief",
                    "rule": "$testBeliefValue",
                    "purpose": "$testPurposeValue"
                }"""

                serialized shouldBeWithTrimming expectedJson
            }

            test("without purpose should serialize correctly") {
                val serialized = json.encodeToString(testBeliefWithoutPurpose)
                val expectedJson = """
                {
                    "type": "Belief",
                    "rule": "$testBeliefValue",
                    "purpose": null
                }"""

                serialized shouldBeWithTrimming expectedJson
            }

            test("with purpose should deserialize correctly") {
                val jsonString = """
                {
                    "type": "Belief",
                    "rule": "$testBeliefValue",
                    "purpose": "$testPurposeValue"
                }"""

                val deserializedBelief = json.decodeFromString<Belief>(jsonString)
                deserializedBelief shouldBe testBeliefWithPurpose
            }

            test("without purpose should deserialize correctly") {
                val jsonString = """
                {
                    "type": "Belief",
                    "rule": "$testBeliefValue",
                    "purpose": null
                }"""

                val deserializedBelief = json.decodeFromString<Belief>(jsonString)
                deserializedBelief shouldBe testBeliefWithoutPurpose
            }

            test("should fail deserialization when the `rule` field is missing") {
                val jsonString = """
                {
                    "type": "Belief",
                    "purpose": "$testPurposeValue"
                }"""

                runCatching {
                    json.decodeFromString<Belief>(jsonString)
                }.isFailure shouldBe true
            }

            test("should fail deserialization when the `value` field is not parsable") {
                val jsonString = """
                {
                    "type": "Belief",
                    "rule": "invalid content structure"
                }"""

                runCatching {
                    json.decodeFromString<Belief>(jsonString)
                }.isFailure shouldBe true
            }
        }
    }
}
