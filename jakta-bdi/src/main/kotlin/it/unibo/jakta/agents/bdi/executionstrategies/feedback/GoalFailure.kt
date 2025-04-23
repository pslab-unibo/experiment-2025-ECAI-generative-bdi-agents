package it.unibo.jakta.agents.bdi.executionstrategies.feedback

import it.unibo.jakta.agents.bdi.Jakta.termFormatter
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
        override val description =
            "Failed to invoke action ${actionSignature.name} with the provided arguments"

        override val metadata = super.metadata + buildMap {
            "action" to actionSignature.name
            "providedArguments" to providedArguments
        }
    }

    data class ActionSubstitutionFailure(
        val actionSignature: Signature,
        val providedArguments: List<Term>,
        override val previousGoals: List<Goal> = emptyList(),
    ) : GoalFailure {
        override val description =
            "Failed substitution of ${actionSignature.name}"

        override val metadata = super.metadata + buildMap {
            "action" to actionSignature.name
            "providedArguments" to providedArguments
        }
    }

    data class ActionNotFound(
        val availableActions: List<Signature>,
        val actionNotFound: String,
        override val previousGoals: List<Goal> = emptyList(),
    ) : GoalFailure {
        override val description =
            "The $actionNotFound action was not found among the available ones"

        override val metadata = super.metadata + buildMap {
            "availableActions" to availableActions.map { it.name }
            "actionNotFound" to actionNotFound
        }
    }

    data class TestGoalFailureFeedback(
        val goalTested: Struct,
        override val previousGoals: List<Goal> = emptyList(),
    ) : GoalFailure {
        override val description =
            "Could not solve for the given goal ${termFormatter.format(goalTested)} with the current belief base"
    }
}
