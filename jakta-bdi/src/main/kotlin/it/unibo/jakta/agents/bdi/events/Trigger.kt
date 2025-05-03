package it.unibo.jakta.agents.bdi.events

import it.unibo.jakta.agents.bdi.Documentable
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.termFormatter
import it.unibo.jakta.agents.bdi.goals.Achieve
import it.unibo.jakta.agents.bdi.goals.Test
import it.unibo.tuprolog.core.Struct
import kotlin.reflect.KClass

/** [Trigger] denotes the change that took place for the [Event] generation. */
sealed interface Trigger : Documentable {
    val value: Struct

    fun copy(
        value: Struct = this.value,
        purpose: String? = this.purpose,
    ): Trigger

    companion object {
        fun fromStruct(
            triggerValue: Struct,
            triggerType: KClass<out Trigger>,
            failure: Boolean = false,
            purpose: String? = null,
        ): Trigger {
            return when (triggerType) {
                BeliefBaseRevision::class -> {
                    if (failure) {
                        BeliefBaseRemoval(Belief.from(triggerValue), purpose)
                    } else {
                        BeliefBaseAddition(Belief.from(triggerValue), purpose)
                    }
                }
                TestGoalTrigger::class -> {
                    if (failure) {
                        TestGoalFailure(triggerValue, purpose)
                    } else {
                        TestGoalInvocation(Belief.from(triggerValue), purpose)
                    }
                }
                AchievementGoalTrigger::class -> {
                    if (failure) {
                        AchievementGoalFailure(triggerValue, purpose)
                    } else {
                        AchievementGoalInvocation(triggerValue, purpose)
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

    fun copy(belief: Belief, purpose: String?): BeliefBaseRevision
}

/** [BeliefBaseRevision] generated after a [Belief] addition to agent's [BeliefBase]. */
class BeliefBaseAddition(
    private val addedBelief: Belief,
    override val purpose: String? = null,
) : BeliefBaseRevision {
    override val value: Struct
        get() = addedBelief.rule.head

    override fun copy(
        value: Struct,
        purpose: String?,
    ): Trigger = BeliefBaseAddition(Belief.from(value), purpose)

    override fun copy(
        belief: Belief,
        purpose: String?,
    ): BeliefBaseRevision = BeliefBaseAddition(belief, purpose)

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
class BeliefBaseRemoval(
    private val removedBelief: Belief,
    override val purpose: String? = null,
) : BeliefBaseRevision {
    override val value: Struct
        get() = removedBelief.rule.head

    override fun copy(
        value: Struct,
        purpose: String?,
    ): Trigger = BeliefBaseRemoval(Belief.from(value), purpose)

    override fun copy(
        belief: Belief,
        purpose: String?,
    ): BeliefBaseRevision = BeliefBaseRemoval(belief, purpose)

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

class BeliefBaseUpdate(
    private val removedBelief: Belief,
    override val purpose: String? = null,
) : BeliefBaseRevision {
    override val value: Struct
        get() = removedBelief.rule.head

    override fun copy(
        value: Struct,
        purpose: String?,
    ): Trigger = BeliefBaseUpdate(Belief.from(value), purpose)

    override fun copy(
        belief: Belief,
        purpose: String?,
    ): BeliefBaseRevision = BeliefBaseUpdate(belief, purpose)

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
    val goal: Struct get() = value
}

/** [TestGoalTrigger] generated after an invocation of a [Test] Goal. */
class TestGoalInvocation(
    val belief: Belief,
    override val purpose: String? = null,
) : TestGoalTrigger {
    override val value: Struct
        get() = belief.rule.head

    override fun copy(
        value: Struct,
        purpose: String?,
    ): Trigger = TestGoalInvocation(Belief.from(value), purpose)

    override fun toString(): String =
        "TestGoalInvocation(value=${termFormatter.format(value)}, purpose=$purpose)"

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
class TestGoalFailure(
    override val value: Struct,
    override val purpose: String? = null,
) : TestGoalTrigger {
    override fun copy(
        value: Struct,
        purpose: String?,
    ): Trigger = TestGoalFailure(value, purpose)

    override fun toString(): String = "TestGoalFailure(value=$value, purpose=$purpose)"

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
class AchievementGoalInvocation(
    override val value: Struct,
    override val purpose: String? = null,
) : AchievementGoalTrigger {
    override fun copy(
        value: Struct,
        purpose: String?,
    ): Trigger = AchievementGoalInvocation(value, purpose)

    override fun toString(): String = "AchievementGoalInvocation(value=$value, purpose=$purpose)"

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
class AchievementGoalFailure(
    override val value: Struct,
    override val purpose: String? = null,
) : AchievementGoalTrigger {
    override fun copy(
        value: Struct,
        purpose: String?,
    ): Trigger = AchievementGoalFailure(value, purpose)

    override fun toString(): String = "AchievementGoalFailure(value=$value, purpose=$purpose)"

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
