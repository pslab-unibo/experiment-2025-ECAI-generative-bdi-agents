package it.unibo.jakta.agents.bdi.executionstrategies.feedback

import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.termFormatter
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Signature

sealed interface GoalFailure : NegativeFeedback {
    val previousGoals: List<Goal>

    data class InvalidActionArityError(
        val actionSignature: Signature,
        val providedArguments: List<Term>,
        override val previousGoals: List<Goal> = emptyList(),
    ) : GoalFailure {
        private val actionName = actionSignature.name

        override val description = "Failed to invoke action $actionName with the provided arguments"

        override val metadata = super.metadata + buildMap {
            "actionName" to actionName
            "providedArguments" to providedArguments.map { termFormatter.format(it) }
        }
    }

    data class ActionSubstitutionFailure(
        val actionSignature: Signature,
        val providedArguments: List<Term>,
        override val previousGoals: List<Goal> = emptyList(),
    ) : GoalFailure {
        private val actionName = actionSignature.name

        override val description = "Failed substitution of $actionName"

        override val metadata = super.metadata + buildMap {
            "actionName" to actionName
            "providedArguments" to providedArguments.map { termFormatter.format(it) }
        }
    }

    data class ActionNotFound(
        val availableActions: List<Signature>,
        val actionNotFoundName: String,
        override val previousGoals: List<Goal> = emptyList(),
    ) : GoalFailure {
        override val description = "The $actionNotFoundName action was not found among the available ones"

        override val metadata = super.metadata + buildMap {
            "availableActions" to availableActions.map { it.name }
            "actionNotFound" to actionNotFoundName
        }
    }

    data class TestGoalFailureFeedback(
        val goalTested: Struct,
        override val previousGoals: List<Goal> = emptyList(),
    ) : GoalFailure {
        private val goal = termFormatter.format(goalTested)

        override val description = "Could not solve for the given goal $goal with the current belief base"
    }
}
