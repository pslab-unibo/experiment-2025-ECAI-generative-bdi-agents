package it.unibo.jakta.agents.bdi.serializers

import io.kotest.matchers.shouldBe
import it.unibo.jakta.agents.bdi.engine.Jakta.dropNumbers
import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.depinjection.JaktaKoin.engineJsonModule
import it.unibo.jakta.agents.bdi.engine.depinjection.JaktaKoin.jsonModule
import it.unibo.jakta.agents.bdi.engine.events.Trigger
import it.unibo.jakta.agents.bdi.engine.formatters.DefaultFormatters.termFormatter
import it.unibo.jakta.agents.bdi.engine.goals.Goal

internal object SerializationTestUtils {
    internal val modules = listOf(engineJsonModule, jsonModule)

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
