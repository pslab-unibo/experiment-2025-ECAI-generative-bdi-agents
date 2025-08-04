package it.unibo.jakta.agents.bdi.serializers

import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import it.unibo.jakta.agents.bdi.engine.goals.Achieve
import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.serializers.SerializationTestUtils.compareGoals
import it.unibo.jakta.agents.bdi.serializers.SerializationTestUtils.modules
import it.unibo.jakta.agents.bdi.serializers.SerializationTestUtils.shouldBeWithTrimming
import it.unibo.tuprolog.core.Struct
import kotlinx.serialization.json.Json
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.getValue

class GoalSerializationTest :
    FunSpec(),
    KoinTest {
    override fun extensions() = listOf(KoinExtension(modules))

    val json by inject<Json>()

    val testGoalJsonValue = "purchase(product123, highPriority)"
    val testGoalValue = Struct.of("purchase", Struct.of("product123"), Struct.of("highPriority"))
    val testGoal = Achieve.of(testGoalValue)

    infix fun Goal.shouldBe(expected: Goal) = compareGoals(this, expected) shouldBe true

    init {
        context("A goal") {
            test("should serialize correctly") {
                val serialized = json.encodeToString<Goal>(testGoal)
                val expectedJson = """
                {
                    "type": "Achieve",
                    "value": "$testGoalJsonValue"
                }"""

                serialized shouldBeWithTrimming expectedJson
            }

            test("should deserialize correctly") {
                val jsonString = """
                {
                    "type": "Achieve",
                    "value": "$testGoalJsonValue",
                    "purpose": null
                }"""

                val deserializedGoal = json.decodeFromString<Goal>(jsonString)
                deserializedGoal shouldBe testGoal
            }

            test("should fail deserialization when the `value` field is missing") {
                val jsonString = """
                {
                    "type": "Achieve"
                }"""

                runCatching {
                    json.decodeFromString<Goal>(jsonString)
                }.isFailure shouldBe true
            }

            test("should fail deserialization when the `value` field is not parsable") {
                val jsonString = """
                {
                    "type": "Achieve",
                    "content": "invalid content structure",
                    "purpose": null
                }"""

                runCatching {
                    json.decodeFromString<Goal>(jsonString)
                }.isFailure shouldBe true
            }
        }
    }
}
