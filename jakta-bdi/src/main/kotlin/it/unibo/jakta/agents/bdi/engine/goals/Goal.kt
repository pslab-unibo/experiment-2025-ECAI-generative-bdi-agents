package it.unibo.jakta.agents.bdi.engine.goals

import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.generation.GenerationConfig
import it.unibo.jakta.agents.bdi.engine.goals.impl.AchieveImpl
import it.unibo.jakta.agents.bdi.engine.goals.impl.ActExternallyImpl
import it.unibo.jakta.agents.bdi.engine.goals.impl.ActImpl
import it.unibo.jakta.agents.bdi.engine.goals.impl.ActInternallyImpl
import it.unibo.jakta.agents.bdi.engine.goals.impl.AddBeliefImpl
import it.unibo.jakta.agents.bdi.engine.goals.impl.GeneratePlanImpl
import it.unibo.jakta.agents.bdi.engine.goals.impl.RemoveBeliefImpl
import it.unibo.jakta.agents.bdi.engine.goals.impl.SpawnImpl
import it.unibo.jakta.agents.bdi.engine.goals.impl.TestImpl
import it.unibo.jakta.agents.bdi.engine.goals.impl.TrackGoalExecutionImpl
import it.unibo.jakta.agents.bdi.engine.goals.impl.UpdateBeliefImpl
import it.unibo.jakta.agents.bdi.engine.plans.PartialPlan
import it.unibo.jakta.agents.bdi.engine.plans.PlanID
import it.unibo.jakta.agents.bdi.engine.plans.PlanLibrary
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableStruct
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
import kotlinx.serialization.Serializable

@Serializable
sealed interface Goal {
    val value: SerializableStruct

    fun applySubstitution(substitution: Substitution): Goal

    fun copy(value: Struct = this.value): Goal
}

@Serializable
class EmptyGoal(
    override val value: SerializableStruct = Truth.TRUE,
) : Goal {
    override fun applySubstitution(substitution: Substitution): Goal = this

    override fun copy(value: Struct): Goal = EmptyGoal(value)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmptyGoal

        return value == other.value
    }

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = "Empty($value)"
}

@Serializable
sealed interface BeliefGoal : Goal {
    val belief: SerializableStruct get() = value
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

@Serializable
sealed interface ActionGoal : Goal {
    val action: SerializableStruct get() = value
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

@Serializable
sealed interface DeclarativeGoal : Goal {
    val goal: Goal
}

interface GeneratePlan : DeclarativeGoal {
    val generationConfig: GenerationConfig?

    fun copy(generationConfig: GenerationConfig): GeneratePlan

    companion object {
        fun of(
            goal: Goal,
            generationConfig: GenerationConfig? = null,
        ): GeneratePlan = GeneratePlanImpl(goal, generationConfig)
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
    fun untrack(
        planID: PlanID,
        planLibrary: PlanLibrary,
    ): PlanLibrary {
        val genPlan =
            planLibrary.plans
                .firstOrNull { it.id == planID } as? PartialPlan
                ?: return planLibrary

        val trackGoals = genPlan.goals.filterIsInstance<TrackGoalExecution>().firstOrNull()
        return if (trackGoals != null) {
            val firstIndex = genPlan.goals.indexOf(trackGoals)
            val filteredGoals =
                genPlan.goals.mapIndexed { idx, goal ->
                    if (idx == firstIndex) trackGoals.goal else goal
                }
            val planWithUntrackedGoal = genPlan.copy(goals = filteredGoals)
            planLibrary
                .removePlan(genPlan)
                .addPlan(planWithUntrackedGoal)
        } else {
            planLibrary
        }
    }
}
