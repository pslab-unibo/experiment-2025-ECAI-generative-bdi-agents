package it.unibo.jakta.agents.bdi

import it.unibo.jakta.agents.bdi.Jakta.JaktaGoalKeyword
import it.unibo.jakta.agents.bdi.Jakta.JaktaTriggerKeyword
import it.unibo.jakta.agents.bdi.Jakta.parseStruct
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.events.AchievementGoalFailure
import it.unibo.jakta.agents.bdi.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.events.BeliefBaseAddition
import it.unibo.jakta.agents.bdi.events.BeliefBaseRemoval
import it.unibo.jakta.agents.bdi.events.BeliefBaseUpdate
import it.unibo.jakta.agents.bdi.events.TestGoalFailure
import it.unibo.jakta.agents.bdi.events.TestGoalInvocation
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Achieve
import it.unibo.jakta.agents.bdi.goals.Act
import it.unibo.jakta.agents.bdi.goals.ActInternally
import it.unibo.jakta.agents.bdi.goals.AddBelief
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.RemoveBelief
import it.unibo.jakta.agents.bdi.goals.Spawn
import it.unibo.jakta.agents.bdi.goals.Test
import it.unibo.jakta.agents.bdi.goals.UpdateBelief
import it.unibo.jakta.nlp.literateprolog.LiteratePrologTemplate
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.parsing.ParseException

object LiteratePrologParser {
    private data class ParseResult(
        val struct: Struct? = null,
        val errorMsg: String? = null,
    )

    private val prologFragmentRegex = "`([^`]+)`".toRegex()

    fun tangleProgram(input: String): List<String> =
        prologFragmentRegex.findAll(input).map { it.groupValues[1] }.toList()

    private fun parse(input: String): ParseResult =
        try {
            val processedInput = convertVariables(input)
            val parsedStruct = parseStruct(processedInput)
            ParseResult(parsedStruct)
        } catch (e: ParseException) {
            ParseResult(errorMsg = e.message)
        }

    private fun convertVariables(content: String): String {
        val regex = Regex("\\[(\\w+)]")
        return regex.replace(content) { match ->
            match.groupValues[1].uppercase()
        }
    }

    fun tangleTrigger(input: String, templates: List<LiteratePrologTemplate> = emptyList()): Trigger? {
        val keyword = JaktaTriggerKeyword.extractTriggerType(input)
        return if (keyword != null) {
            val processedInput = input.removePrefix(keyword.toString().lowercase())
            val parsedInput = tangleStruct(processedInput, templates)
            when (keyword) {
                JaktaTriggerKeyword.Achieve -> parsedInput?.let { AchievementGoalInvocation(it) }
                JaktaTriggerKeyword.AchieveFailure -> parsedInput?.let { AchievementGoalFailure(it) }
                JaktaTriggerKeyword.Test -> parsedInput?.let { TestGoalInvocation(it) }
                JaktaTriggerKeyword.TestFailure -> parsedInput?.let { TestGoalFailure(it) }
                JaktaTriggerKeyword.AddBelief -> parsedInput?.let { Belief.from(it) }?.let { BeliefBaseAddition(it) }
                JaktaTriggerKeyword.RemoveBelief -> parsedInput?.let { Belief.from(it) }?.let { BeliefBaseRemoval(it) }
                JaktaTriggerKeyword.UpdateBelief -> parsedInput?.let { Belief.from(it) }?.let { BeliefBaseUpdate(it) }
            }
        } else {
            null
        }
    }

    fun tangleGoal(input: String, templates: List<LiteratePrologTemplate> = emptyList()): Goal? {
        val keyword = JaktaGoalKeyword.extractGoalType(input)
        return if (keyword != null) {
            val processedInput = input.removePrefix(keyword.toString().lowercase())
            when (keyword) {
                JaktaGoalKeyword.Achieve -> tangleStruct(processedInput, templates)?.let { Achieve.of(it) }
                JaktaGoalKeyword.Test -> tangleStruct(
                    processedInput,
                    templates,
                )?.let { Belief.from(it) }?.let { Test.of(it) }
                JaktaGoalKeyword.Spawn -> tangleGoal(processedInput, templates)?.let { Spawn.of(it) }
                JaktaGoalKeyword.Generate -> tangleStruct(
                    processedInput,
                    templates,
                )?.let { Generate.of(it, processedInput) }
                JaktaGoalKeyword.Add -> tangleStruct(
                    processedInput,
                    templates,
                )?.let { Belief.from(it) }?.let { AddBelief.of(it) }
                JaktaGoalKeyword.Remove -> tangleStruct(
                    processedInput,
                    templates,
                )?.let { Belief.from(it) }?.let { RemoveBelief.of(it) }
                JaktaGoalKeyword.Update -> tangleStruct(
                    processedInput,
                    templates,
                )?.let { Belief.from(it) }?.let { UpdateBelief.of(it) }
                JaktaGoalKeyword.Execute -> tangleStruct(processedInput, templates)?.let { Act.of(it) }
                JaktaGoalKeyword.Iact -> tangleStruct(processedInput, templates)?.let { ActInternally.of(it) }
            }
        } else {
            null
        }
    }

    fun tangleGoals(input: String, templates: List<LiteratePrologTemplate> = emptyList()): List<Goal> {
        val structsInsideBackticks = tangleProgram(input).mapNotNull { tangleGoal(it, templates) }
        return if (structsInsideBackticks.isNotEmpty()) {
            structsInsideBackticks
        } else {
            tangleGoal(input)?.let { listOf(it) } ?: emptyList()
        }
    }

    /*
     * Always try first to parse a struct from a template if any are available.
     * If the parsing fails, try to parse the string as prolog.
     */
    fun tangleStruct(
        input: String,
        templates: List<LiteratePrologTemplate> = emptyList(),
    ): Struct? = if (templates.isNotEmpty()) {
        tangleFact(input, templates) ?: parse(input).struct
    } else {
        parse(input).struct
    }

    fun tangleStructs(
        input: String,
        templates: List<LiteratePrologTemplate> = emptyList(),
    ): List<Struct> {
        val structsFromFragments = tangleProgram(input).mapNotNull { tangleStruct(it, templates) }
        return structsFromFragments.ifEmpty { tangleStruct(input, templates)?.let { listOf(it) } ?: emptyList() }
    }

    private fun buildFact(input: String, templates: List<LiteratePrologTemplate>): String? =
        templates.asSequence().filter { it.matches(input) }.firstOrNull()?.buildPredicate(input)

    private fun tangleFact(input: String, templates: List<LiteratePrologTemplate>): Struct? =
        buildFact(input, templates)?.let { tangleStruct(it) }

    /**
     * Match expressions of type `variable operator number` in the given
     * input and create an additional string representing a variable declaration
     * to be added for each match.
     *
     * Also rewrite the goal to use the variable declared in the guard.
     *
     * For example, given the string `count(0, [n+1])`
     * return `count(0, [n])` and `[n1] is [n] + 1`
     */
    fun processArithmeticExpressions(input: String): Pair<String, List<String>> {
        val pattern = Regex("""(\w+)\s*([+\-*/%^]|>=|<=|>|<|\*\*)\s*(\d+)""")
        val matches = pattern.findAll(input)

        val extraGuards = mutableListOf<String>()
        var processedGoal = input

        for (match in matches) {
            val variable = match.groupValues[1]
            val operator = match.groupValues[2]
            val number = match.groupValues[3]

            processedGoal = processedGoal.replace(match.value, "${variable}1")

            val output2 = "[${variable}1] is [$variable] $operator $number"
            extraGuards.add(output2)
        }

        return Pair(processedGoal, extraGuards)
    }
}
