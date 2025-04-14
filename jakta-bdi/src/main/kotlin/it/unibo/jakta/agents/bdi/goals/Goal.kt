package it.unibo.jakta.agents.bdi.goals

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.goals.impl.AchieveImpl
import it.unibo.jakta.agents.bdi.goals.impl.ActExternallyImpl
import it.unibo.jakta.agents.bdi.goals.impl.ActImpl
import it.unibo.jakta.agents.bdi.goals.impl.ActInternallyImpl
import it.unibo.jakta.agents.bdi.goals.impl.AddBeliefImpl
import it.unibo.jakta.agents.bdi.goals.impl.GenerateImpl
import it.unibo.jakta.agents.bdi.goals.impl.RemoveBeliefImpl
import it.unibo.jakta.agents.bdi.goals.impl.SpawnImpl
import it.unibo.jakta.agents.bdi.goals.impl.TestImpl
import it.unibo.jakta.agents.bdi.goals.impl.TrackGoalExecutionImpl
import it.unibo.jakta.agents.bdi.goals.impl.TrackPlanExecutionImpl
import it.unibo.jakta.agents.bdi.goals.impl.UpdateBeliefImpl
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.jakta.agents.bdi.plans.copy
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth

sealed interface Goal {
    val value: Struct

    fun applySubstitution(substitution: Substitution): Goal

    fun copy(value: Struct = this.value): Goal
}

class EmptyGoal(override val value: Struct = Truth.TRUE) : Goal {
    override fun applySubstitution(substitution: Substitution): Goal = this

    override fun copy(value: Struct): Goal = EmptyGoal(value)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmptyGoal

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

sealed interface BeliefGoal : Goal {
    val belief: Struct
        get() = value
}

interface AddBelief : BeliefGoal {
    companion object {
        fun of(belief: Belief): AddBelief = AddBeliefImpl(belief)
    }
}

interface RemoveBelief : BeliefGoal {
    companion object {
        fun of(belief: Belief): RemoveBelief = RemoveBeliefImpl(belief)
    }
}

interface UpdateBelief : BeliefGoal {
    companion object {
        fun of(belief: Belief): UpdateBelief = UpdateBeliefImpl(belief)
    }
}

interface Achieve : Goal {
    companion object {
        fun of(value: Struct): Achieve = AchieveImpl(value)
    }
}

interface Test : Goal {
    companion object {
        fun of(belief: Belief): Test = TestImpl(belief)
    }
}

interface Spawn : Goal {
    val goal: Goal
    companion object {
        fun of(goal: Goal): Spawn = SpawnImpl(goal)
    }
}

sealed interface ActionGoal : Goal {
    val action: Struct
        get() = value
}

interface Act : ActionGoal {
    companion object {
        fun of(value: Struct): Act = ActImpl(value)
    }
}

interface ActInternally : ActionGoal {
    companion object {
        fun of(value: Struct): ActInternally = ActInternallyImpl(value)
    }
}

interface ActExternally : ActionGoal {
    companion object {
        fun of(value: Struct): ActExternally = ActExternallyImpl(value)
    }
}

sealed interface DeclarativeGoal : Goal

interface Generate : DeclarativeGoal {
    val literateValue: String

    companion object {
        fun of(value: Struct, literateValue: String = formatter.format(value)): Generate =
            GenerateImpl(value, literateValue)
    }
}

sealed interface TrackGoal : Goal {
    val planID: PlanID
}

interface TrackGoalExecution : TrackGoal {
    val goal: Goal
    companion object {
        fun of(planID: PlanID, goal: Goal): TrackGoalExecution = TrackGoalExecutionImpl(planID, goal)
    }

    /**
     * Replace the first [TrackGoalExecution] found with the goal it is tracking.
     */
    fun untrack(planLibrary: PlanLibrary): PlanLibrary {
        val genPlan = planLibrary.plans
            .firstOrNull { it.id == this.planID } as? PartialPlan
            ?: return planLibrary

        val r = genPlan.goals.filterIsInstance<TrackGoalExecution>().first()
        val firstIndex = genPlan.goals.indexOf(r)
        val filteredGoals = genPlan.goals.mapIndexed { idx, goal ->
            if (idx == firstIndex) r.goal else goal
        }

        val planWithUntrackedGoal = genPlan.copy(goals = filteredGoals)
        return planLibrary
            .removePlan(genPlan)
            .addPlan(planWithUntrackedGoal)
    }
}

interface TrackPlanExecution : TrackGoal {
    companion object {
        fun of(planID: PlanID): TrackPlanExecution = TrackPlanExecutionImpl(planID)
    }
}
