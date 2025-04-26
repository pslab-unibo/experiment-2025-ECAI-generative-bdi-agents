package it.unibo.jakta.agents.bdi.goals

import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.goals.impl.AchieveImpl
import it.unibo.jakta.agents.bdi.goals.impl.ActExternallyImpl
import it.unibo.jakta.agents.bdi.goals.impl.ActImpl
import it.unibo.jakta.agents.bdi.goals.impl.ActInternallyImpl
import it.unibo.jakta.agents.bdi.goals.impl.AddBeliefImpl
import it.unibo.jakta.agents.bdi.goals.impl.GeneratePlanImpl
import it.unibo.jakta.agents.bdi.goals.impl.RemoveBeliefImpl
import it.unibo.jakta.agents.bdi.goals.impl.SpawnImpl
import it.unibo.jakta.agents.bdi.goals.impl.TestImpl
import it.unibo.jakta.agents.bdi.goals.impl.TrackGoalExecutionImpl
import it.unibo.jakta.agents.bdi.goals.impl.UpdateBeliefImpl
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
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
    val belief: Belief
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

sealed interface DeclarativeGoal : Goal {
    val goal: Goal
}

interface GeneratePlan : DeclarativeGoal {
    companion object {
        fun of(goal: Goal): GeneratePlan = GeneratePlanImpl(goal)
    }
}

interface TrackGoalExecution : Goal {
    val goal: Goal

    companion object {
        fun of(goal: Goal): TrackGoalExecution = TrackGoalExecutionImpl(goal)
    }

    /**
     * Replace the first [TrackGoalExecution] found with the goal it is tracking.
     */
    fun untrack(planID: PlanID, planLibrary: PlanLibrary): PlanLibrary {
        val genPlan = planLibrary.plans
            .firstOrNull { it.id == planID } as? PartialPlan
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
