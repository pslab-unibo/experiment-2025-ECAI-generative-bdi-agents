package it.unibo.jakta.playground.evaluation.plandata

import it.unibo.jakta.agents.bdi.engine.actions.ActionSignature
import it.unibo.jakta.agents.bdi.engine.actions.effects.AdmissibleBeliefChange
import it.unibo.jakta.agents.bdi.engine.actions.effects.AdmissibleGoalChange
import it.unibo.jakta.agents.bdi.engine.actions.effects.PlanChange
import it.unibo.jakta.agents.bdi.engine.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.engine.context.ContextUpdate.ADDITION
import it.unibo.jakta.agents.bdi.engine.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.engine.logging.events.ActionEvent.ActionAddition
import it.unibo.jakta.agents.bdi.engine.logging.loggers.JaktaLogger.Companion.extractLastId
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.playground.evaluation.FileProcessor.processFile
import java.io.File

data class InvocationContext(
    val masId: String?,
    val agentId: String?,
    val plans: List<Plan> = emptyList(),
    val admissibleGoals: List<AdmissibleGoal> = emptyList(),
    val admissibleBeliefs: List<AdmissibleBelief> = emptyList(),
    val actions: List<ActionSignature> = emptyList(),
) {
    override fun toString(): String =
        """
        |InvocationContext:
        |  Plans: ${plans.size}
        |  Admissible Goals: ${admissibleGoals.size}
        |  Admissible Beliefs: ${admissibleBeliefs.size}
        |  Actions: ${actions.size}
        """.trimMargin()

    companion object {
        fun from(
            masLogFile: File,
            agentLogFile: File,
        ): InvocationContext {
            val actions = mutableListOf<ActionSignature>()

            processFile(masLogFile) { logEntry ->
                val event = logEntry.message.event
                if (event is ActionAddition) {
                    event.action?.let { actions.add(it.actionSignature) }
                }
                true
            }

            val plans = mutableListOf<Plan>()
            val admissibleGoals = mutableListOf<AdmissibleGoal>()
            val admissibleBeliefs = mutableListOf<AdmissibleBelief>()

            processFile(agentLogFile) { logEntry ->
                val event = logEntry.message.event
                when (val ev = event) {
                    is ActionAddition -> actions.add(ev.actionSignature)
                    is PlanChange if ev.changeType == ADDITION -> plans.add(ev.plan)
                    is AdmissibleGoalChange if ev.changeType == ADDITION -> admissibleGoals.add(ev.goal)
                    is AdmissibleBeliefChange if ev.changeType == ADDITION -> admissibleBeliefs.add(ev.belief)
                }
                true
            }

            val masId = extractLastId(masLogFile.name)
            val agentId = extractLastId(agentLogFile.name)

            return InvocationContext(
                masId = masId,
                agentId = agentId,
                plans = plans,
                admissibleGoals = admissibleGoals,
                admissibleBeliefs = admissibleBeliefs,
                actions = actions,
            )
        }
    }
}
