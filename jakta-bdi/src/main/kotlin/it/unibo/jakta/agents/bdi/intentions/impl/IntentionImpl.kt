package it.unibo.jakta.agents.bdi.intentions.impl

import it.unibo.jakta.agents.bdi.intentions.IntentionID
import it.unibo.jakta.agents.bdi.plans.ActivationRecord

internal class IntentionImpl(
    override val recordStack: List<ActivationRecord>,
    override val isSuspended: Boolean = false,
    override val id: IntentionID = IntentionID(),
) : BaseIntention(recordStack, isSuspended, id)
