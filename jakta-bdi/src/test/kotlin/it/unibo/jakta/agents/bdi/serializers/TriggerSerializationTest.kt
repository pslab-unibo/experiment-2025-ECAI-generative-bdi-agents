package it.unibo.jakta.agents.bdi.serializers

import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import it.unibo.jakta.agents.bdi.engine.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.engine.events.Trigger
import it.unibo.jakta.agents.bdi.serializers.SerializationTestUtils.compareTriggers
import it.unibo.jakta.agents.bdi.serializers.SerializationTestUtils.modules
import it.unibo.jakta.agents.bdi.serializers.SerializationTestUtils.shouldBeWithTrimming
import it.unibo.tuprolog.core.Struct
import kotlinx.serialization.json.Json
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.getValue

class TriggerSerializationTest :
    FunSpec(),
    KoinTest {
    override fun extensions() = listOf(KoinExtension(modules))

    val json by inject<Json>()
    val testTriggerJsonValue = "customer(john, member)"
    val testTriggerPurpose = "customer registration"
    val testTriggerValue = Struct.of("customer", Struct.of("john"), Struct.of("member"))
    val testTriggerWithPurpose = AchievementGoalInvocation(testTriggerValue, testTriggerPurpose)
    val testTriggerWithoutPurpose = AchievementGoalInvocation(testTriggerValue)

    infix fun Trigger.shouldBe(expected: Trigger) = compareTriggers(this, expected) shouldBe true

    init {
        context("A trigger") {
            test("with a purpose should serialize correctly") {
                val serialized = json.encodeToString<Trigger>(testTriggerWithPurpose)
                val expectedJson = """
                {
                    "type": "AchievementGoalInvocation",
                    "value": "$testTriggerJsonValue",
                    "purpose": "$testTriggerPurpose"
                }"""

                serialized shouldBeWithTrimming expectedJson
            }

            test("without purpose should serialize correctly") {
                val serialized = json.encodeToString<Trigger>(testTriggerWithoutPurpose)
                val expectedJson = """
                {
                    "type": "AchievementGoalInvocation",
                    "value": "$testTriggerJsonValue",
                    "purpose": null
                }"""

                serialized shouldBeWithTrimming expectedJson
            }

            test("with a purpose should deserialize correctly") {
                val jsonString = """
                {
                    "type": "AchievementGoalInvocation",
                    "value": "$testTriggerJsonValue",
                    "purpose": "$testTriggerPurpose"
                }"""

                val deserializedTrigger = json.decodeFromString<Trigger>(jsonString)
                deserializedTrigger shouldBe testTriggerWithPurpose
            }

            test("without purpose should deserialize correctly") {
                val jsonString = """
                {
                    "type": "AchievementGoalInvocation",
                    "value": "$testTriggerJsonValue",
                    "purpose": null
                }"""

                val deserializedTrigger = json.decodeFromString<Trigger>(jsonString)
                deserializedTrigger shouldBe testTriggerWithoutPurpose
            }

            test("should fail deserialization when the `value` field is missing") {
                val jsonString = """
                {
                    "purpose": "$testTriggerPurpose"
                }"""

                runCatching {
                    json.decodeFromString<Trigger>(jsonString)
                }.isFailure shouldBe true
            }

            test("should fail deserialization when the `value` field is not parsable") {
                val jsonString = """
                {
                    "value": "invalid content structure"
                }"""

                runCatching {
                    json.decodeFromString<Trigger>(jsonString)
                }.isFailure shouldBe true
            }
        }
    }
}
