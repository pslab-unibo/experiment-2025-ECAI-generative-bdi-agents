package it.unibo.jakta.agents.bdi.goals.impl

import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.goals.Achieve
import it.unibo.jakta.agents.bdi.goals.Act
import it.unibo.jakta.agents.bdi.goals.ActExternally
import it.unibo.jakta.agents.bdi.goals.ActInternally
import it.unibo.jakta.agents.bdi.goals.AddBelief
import it.unibo.jakta.agents.bdi.goals.EmptyGoal
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.RemoveBelief
import it.unibo.jakta.agents.bdi.goals.Spawn
import it.unibo.jakta.agents.bdi.goals.Test
import it.unibo.jakta.agents.bdi.goals.UpdateBelief
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution

internal class GenerateImpl(override val goal: Goal) : Generate {
    override val value: Struct
        get() = goal.value

    override fun applySubstitution(substitution: Substitution) =
        AchieveImpl(value.apply(substitution).castToStruct())

    override fun toString(): String = "Generate($goal)"

    override fun copy(value: Struct): Goal = when (goal) {
        is Achieve -> Achieve.of(value)
        is Act -> Act.of(value)
        is ActExternally -> ActExternally.of(value)
        is ActInternally -> ActInternally.of(value)
        is AddBelief -> AddBelief.of(Belief.from(value))
        is RemoveBelief -> RemoveBelief.of(Belief.from(value))
        is UpdateBelief -> UpdateBelief.of(Belief.from(value))
        is EmptyGoal -> EmptyGoal(value)
        is Generate -> Generate.of(goal)
        is Spawn -> Spawn.of(value)
        is Test -> Test.of(Belief.from(value))
    }.let { GenerateImpl(it) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AchieveImpl
        if (value != other.value) return false
        return true
    }

    override fun hashCode(): Int = value.hashCode()
}
