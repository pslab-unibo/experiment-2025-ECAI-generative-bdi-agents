package it.unibo.jakta.generationstrategies.lm.pipeline.parsing

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
import it.unibo.jakta.agents.bdi.goals.AddBelief
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.RemoveBelief
import it.unibo.jakta.agents.bdi.goals.Spawn
import it.unibo.jakta.agents.bdi.goals.Test
import it.unibo.jakta.agents.bdi.goals.UpdateBelief
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ParsingUtils.GoalKeyword
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ParsingUtils.GoalKeyword.Companion.extractGoalType
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ParsingUtils.TriggerKeyword
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ParsingUtils.TriggerKeyword.Companion.extractTriggerType
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.parsing.ParseException

object JaktaParser {
    private data class PrologParserResult(
        val struct: Struct? = null,
        val errorMsg: String? = null,
    )

    private fun parse(input: String): PrologParserResult =
        try {
            val processedInput = convertVariables(input)
            val parsedStruct = parseStruct(processedInput)
            PrologParserResult(parsedStruct)
        } catch (e: ParseException) {
            PrologParserResult(errorMsg = e.message)
        }

    private fun convertVariables(content: String): String {
        val regex = Regex("\\[(\\w+)]")
        return regex.replace(content) { match ->
            match.groupValues[1].uppercase()
        }
    }

    fun tangleTrigger(input: String): Trigger? {
        val keyword = extractTriggerType(input)
        return if (keyword != null) {
            val processedInput = input.removePrefix(keyword.toString().lowercase())
            val parsedInput = tangleStruct(processedInput)
            when (keyword) {
                TriggerKeyword.Achieve -> parsedInput?.let { AchievementGoalInvocation(it) }
                TriggerKeyword.AchieveFailure -> parsedInput?.let { AchievementGoalFailure(it) }
                TriggerKeyword.Test -> parsedInput?.let { TestGoalInvocation(it) }
                TriggerKeyword.TestFailure -> parsedInput?.let { TestGoalFailure(it) }
                TriggerKeyword.AddBelief -> parsedInput?.let { Belief.from(it) }?.let { BeliefBaseAddition(it) }
                TriggerKeyword.RemoveBelief -> parsedInput?.let { Belief.from(it) }?.let { BeliefBaseRemoval(it) }
                TriggerKeyword.UpdateBelief -> parsedInput?.let { Belief.from(it) }?.let { BeliefBaseUpdate(it) }
            }
        } else {
            null
        }
    }

    fun tangleGoal(input: String): Goal? {
        val keyword = extractGoalType(input)
        return if (keyword != null) {
            val processedInput = input.removePrefix(keyword.toString().lowercase())
            when (keyword) {
                GoalKeyword.Spawn -> tangleGoal(processedInput)?.let { Spawn.of(it) }
                else -> {
                    val parsedInput = tangleStruct(processedInput)
                    when (keyword) {
                        GoalKeyword.Achieve -> parsedInput?.let { Achieve.of(it) }
                        GoalKeyword.Test -> parsedInput?.let { Belief.from(it) }?.let { Test.of(it) }
                        GoalKeyword.Generate -> parsedInput?.let { GeneratePlan.of(it) }
                        GoalKeyword.Add -> parsedInput?.let { Belief.from(it) }?.let { AddBelief.of(it) }
                        GoalKeyword.Remove -> parsedInput?.let { Belief.from(it) }?.let { RemoveBelief.of(it) }
                        GoalKeyword.Update -> parsedInput?.let { Belief.from(it) }?.let { UpdateBelief.of(it) }
                        GoalKeyword.Execute -> parsedInput?.let { Act.of(it) }
                        else -> null
                    }
                }
            }
        } else {
            null
        }
    }

    fun tangleStruct(input: String): Struct? = parse(input).struct
}
