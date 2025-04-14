package it.unibo.jakta.agents.bdi.plangeneration.feedback

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.actions.LiterateSignature
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

sealed interface GoalFailure : FailureFeedback {
    val previousGoals: List<Goal>
}

data class InvalidActionArityError(
    val actionSignature: LiterateSignature,
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
    val actionSignature: LiterateSignature,
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
    val availableActions: List<LiterateSignature>,
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
        "Could not solve for the given goal ${formatter.format(goalTested)} with the current belief base"
}
