package it.unibo.jakta.agents.bdi.serializers

import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import it.unibo.jakta.agents.bdi.engine.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.engine.goals.Achieve
import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.engine.plans.PlanID
import it.unibo.jakta.agents.bdi.serializers.SerializationTestUtils.modules
import it.unibo.jakta.agents.bdi.serializers.SerializationTestUtils.shouldBeWithTrimming
import it.unibo.tuprolog.core.Struct
import kotlinx.serialization.json.Json
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.getValue

class PlanSerializationTest :
    FunSpec(),
    KoinTest {
    override fun extensions() = listOf(KoinExtension(modules))

    val json by inject<Json>()

    val testGoalValue = Struct.of("purchase", Struct.of("product123"), Struct.of("highPriority"))
    val testTrigger = AchievementGoalInvocation(testGoalValue)
    val testGoal: Goal = Achieve.of(testGoalValue)
    val testPlan = Plan.of(PlanID(testTrigger, testGoalValue), listOf(testGoal))

    init {

        context("A plan") {
            test("should serialize correctly") {
                val serialized = json.encodeToString<Plan>(testPlan)
                val jsonString = """
                {
                    "type": "Plan",
                    "trigger": {
                        "type": "AchievementGoalInvocation",
                        "value": "purchase(product123, highPriority)"
                    },
                    "guard": "purchase(product123, highPriority)",
                    "goals": [
                        {
                            "type": "Achieve",
                            "value": "purchase(product123, highPriority)"
                        }
                    ]
                }"""

                serialized shouldBeWithTrimming jsonString
            }

            test("should deserialize correctly") {
                val jsonString = """
                {
                    "type": "Plan",
                    "trigger": {
                        "type": "AchievementGoalInvocation",
                        "value": "purchase(product123, highPriority)"
                    },
                    "guard": "purchase(product123, highPriority)",
                    "goals": [
                        {
                            "type": "Achieve",
                            "value": "purchase(product123, highPriority)"
                        }
                    ]
                }"""

                val deserializedPlan = json.decodeFromString<Plan>(jsonString)
                deserializedPlan shouldBe testPlan
            }

            test("should fail deserialization when the `trigger` field is missing") {
                val jsonString = """
               {
                    "type": "Plan",
                    "guard": "purchase(product123, highPriority)",
                    "goals": [
                        {
                            "type": "Achieve",
                            "value": "purchase(product123, highPriority)"
                        }
                    ]
                }"""

                runCatching {
                    json.decodeFromString<Plan>(jsonString)
                }.isFailure shouldBe true
            }

            test("should fail deserialization when the `guard` field is missing") {
                val jsonString = """
               {
                    "type": "Plan",
                    "trigger": {
                        "type": "AchievementGoalInvocation",
                        "value": "purchase(product123, highPriority)"
                    },
                    "goals": [
                        {
                            "type": "Achieve",
                            "value": "purchase(product123, highPriority)"
                        }
                    ]
                }"""

                runCatching {
                    json.decodeFromString<Plan>(jsonString)
                }.isFailure shouldBe true
            }

            test("should fail deserialization when the `goals` field is missing") {
                val jsonString = """
               {
                    "type": "Plan",
                    "trigger": {
                        "type": "AchievementGoalInvocation",
                        "value": "purchase(product123, highPriority)"
                    },
                    "guard": "purchase(product123, highPriority)"
                }"""

                runCatching {
                    json.decodeFromString<Plan>(jsonString)
                }.isFailure shouldBe true
            }

            test("should throw exception when the value is not parsable") {
                val jsonString = """
                {
                    "type": "Plan",
                    "trigger": {
                        "type": "AchievementGoalInvocation",
                        "value": "purchase(product123, highPriority)"
                    },
                    "guard": "test purchase(product123, highPriority)",
                    "goals": [
                        {
                            "type": "Achieve",
                            "value": "purchase(product123, highPriority)"
                        }
                    ]
                }"""

                runCatching {
                    json.decodeFromString<Plan>(jsonString)
                }.isFailure shouldBe true
            }
        }
    }
}
