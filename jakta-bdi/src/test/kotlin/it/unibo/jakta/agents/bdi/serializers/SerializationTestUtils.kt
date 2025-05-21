package it.unibo.jakta.agents.bdi.serializers

import io.kotest.matchers.shouldBe
import it.unibo.jakta.agents.bdi.engine.Jakta.dropNumbers
import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.events.Trigger
import it.unibo.jakta.agents.bdi.engine.formatters.DefaultFormatters.termFormatter
import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.serialization.modules.JsonModule
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializersModuleProvider
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import org.koin.ksp.generated.module

internal object SerializationTestUtils {
    val jsonModule =
        module {
            single {
                val modules = GlobalContext.get().getAll<SerializersModuleProvider>()
                val combined = SerializersModule { modules.forEach { include(it.modules) } }
                Json { serializersModule = combined }
            }
        }

    internal val modules = listOf(JsonModule().module, jsonModule)

    private fun String.normalizeJsonWhitespace(): String =
        this.trim().replace("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)".toRegex(), "")

    internal infix fun String.shouldBeWithTrimming(expected: String) {
        this.normalizeJsonWhitespace() shouldBe expected.trimIndent().normalizeJsonWhitespace()
    }

    internal fun compareBeliefs(
        actual: Belief,
        expected: Belief,
    ): Boolean =
        termFormatter.format(actual.rule).dropNumbers() ==
            termFormatter.format(expected.rule).dropNumbers() &&
            actual.purpose == expected.purpose

    internal fun compareTriggers(
        actual: Trigger,
        expected: Trigger,
    ): Boolean =
        termFormatter.format(actual.value).dropNumbers() ==
            termFormatter.format(expected.value).dropNumbers() &&
            actual.purpose == expected.purpose

    internal fun compareGoals(
        actual: Goal,
        expected: Goal,
    ): Boolean =
        termFormatter.format(actual.value).dropNumbers() ==
            termFormatter.format(expected.value).dropNumbers()
}
