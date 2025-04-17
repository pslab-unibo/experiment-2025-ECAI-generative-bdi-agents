package it.unibo.jakta.generationstrategies.lm.pipeline.formatting.impl

import it.unibo.jakta.agents.bdi.Jakta.wrapWithDelimiters
import it.unibo.jakta.agents.bdi.events.AchievementGoalFailure
import it.unibo.jakta.agents.bdi.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.events.BeliefBaseAddition
import it.unibo.jakta.agents.bdi.events.BeliefBaseRemoval
import it.unibo.jakta.agents.bdi.events.BeliefBaseUpdate
import it.unibo.jakta.agents.bdi.events.TestGoalFailure
import it.unibo.jakta.agents.bdi.events.TestGoalInvocation
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.TriggerFormatter
import it.unibo.tuprolog.core.TermFormatter

class TriggerFormatterImpl(val termFormatter: TermFormatter) : TriggerFormatter {
    override fun format(trigger: Trigger): String {
        val goal = termFormatter.format(trigger.value)
        return when (trigger) {
            is AchievementGoalInvocation -> "achieve $goal"
            is AchievementGoalFailure -> "achieve failure $goal"
            is TestGoalInvocation -> "test $goal"
            is TestGoalFailure -> "test failure $goal"
            is BeliefBaseAddition -> "belief addition $goal "
            is BeliefBaseRemoval -> "belief removal $goal"
            is BeliefBaseUpdate -> "belief update $goal"
            else -> goal
        }.wrapWithDelimiters()
    }
}
