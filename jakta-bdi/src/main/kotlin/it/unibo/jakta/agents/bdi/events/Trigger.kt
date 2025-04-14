package it.unibo.jakta.agents.bdi.events

import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.goals.Achieve
import it.unibo.jakta.agents.bdi.goals.Test
import it.unibo.tuprolog.core.Struct
import kotlin.reflect.KClass

/** [Trigger] denotes the change that took place for the [Event] generation. */
interface Trigger {
    val value: Struct

    fun copy(value: Struct = this.value): Trigger

    companion object {
        fun fromStruct(
            trigger: Struct,
            triggerType: KClass<out Trigger>,
            failure: Boolean = false,
        ): Trigger {
            return when (triggerType) {
                BeliefBaseRevision::class -> {
                    if (failure) {
                        BeliefBaseRemoval(Belief.from(trigger))
                    } else {
                        BeliefBaseAddition(Belief.from(trigger))
                    }
                }
                TestGoalTrigger::class -> {
                    if (failure) {
                        TestGoalFailure(trigger)
                    } else {
                        TestGoalInvocation(trigger)
                    }
                }
                AchievementGoalTrigger::class -> {
                    if (failure) {
                        AchievementGoalFailure(trigger)
                    } else {
                        AchievementGoalInvocation(trigger)
                    }
                }
                else -> throw IllegalArgumentException("Unknown trigger type: $triggerType")
            }
        }
    }
}

/** [Trigger] generated after a [Belief] addition (or removal) from the [BeliefBase]. */
interface BeliefBaseRevision : Trigger {

    /** The head of the [Belief] that is inserted (or removed) from the [BeliefBase]. */
    val belief: Struct
        get() = value

    fun copy(belief: Belief): BeliefBaseRevision
}

/** [BeliefBaseRevision] generated after a [Belief] addition to agent's [BeliefBase]. */
class BeliefBaseAddition(private val addedBelief: Belief) : BeliefBaseRevision {
    override val value: Struct
        get() = addedBelief.rule.head

    override fun copy(value: Struct): Trigger = BeliefBaseAddition(Belief.from(value))

    override fun copy(belief: Belief): BeliefBaseRevision = BeliefBaseAddition(belief)

    override fun toString(): String = "BeliefBaseAddition(value=$value)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BeliefBaseAddition

        if (addedBelief != other.addedBelief) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = addedBelief.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}

/** [BeliefBaseRevision] generated after a [Belief] removal from agent's [BeliefBase]. */
class BeliefBaseRemoval(private val removedBelief: Belief) : BeliefBaseRevision {
    override val value: Struct
        get() = removedBelief.rule.head

    override fun copy(value: Struct): Trigger = BeliefBaseRemoval(Belief.from(value))

    override fun copy(belief: Belief): BeliefBaseRevision = BeliefBaseRemoval(belief)

    override fun toString(): String = "BeliefBaseRemoval(value=$value)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BeliefBaseRemoval

        if (removedBelief != other.removedBelief) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = removedBelief.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}

class BeliefBaseUpdate(private val removedBelief: Belief) : BeliefBaseRevision {
    override val value: Struct
        get() = removedBelief.rule.head

    override fun copy(value: Struct): Trigger = BeliefBaseUpdate(Belief.from(value))

    override fun copy(belief: Belief): BeliefBaseRevision = BeliefBaseUpdate(belief)

    override fun toString(): String = "BeliefBaseUpdate(value=$value)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BeliefBaseUpdate

        if (removedBelief != other.removedBelief) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = removedBelief.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}

/** [Trigger] of an event made by a [Test] Goal. */
interface TestGoalTrigger : Trigger {
    val goal: Struct
        get() = value
}

/** [TestGoalTrigger] generated after an invocation of a [Test] Goal. */
class TestGoalInvocation(override val value: Struct) : TestGoalTrigger {
    override fun copy(value: Struct): Trigger = TestGoalInvocation(value)

    override fun toString(): String = "TestGoalInvocation(value=$value)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestGoalInvocation

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

/** [TestGoalTrigger] generated after a failure of a [Test] Goal. */
class TestGoalFailure(override val value: Struct) : TestGoalTrigger {
    override fun copy(value: Struct): Trigger = TestGoalFailure(value)

    override fun toString(): String = "TestGoalFailure(value=$value)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestGoalFailure

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

/** [Trigger] of an event made by a [Achieve] Goal. */
interface AchievementGoalTrigger : Trigger {
    val goal: Struct
        get() = value
}

/** [AchievementGoalTrigger] generated after the invocation of a [Achieve] Goal. */
class AchievementGoalInvocation(override val value: Struct) : AchievementGoalTrigger {
    override fun copy(value: Struct): Trigger = AchievementGoalInvocation(value)

    override fun toString(): String = "AchievementGoalInvocation(value=$value)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AchievementGoalInvocation

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

/** [AchievementGoalTrigger] generated after the failure of a [Achieve] Goal. */
class AchievementGoalFailure(override val value: Struct) : AchievementGoalTrigger {
    override fun copy(value: Struct): Trigger = AchievementGoalFailure(value)

    override fun toString(): String = "AchievementGoalFailure(value=$value)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AchievementGoalFailure

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
