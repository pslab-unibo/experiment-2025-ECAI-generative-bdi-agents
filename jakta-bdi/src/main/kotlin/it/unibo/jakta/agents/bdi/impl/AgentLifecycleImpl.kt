package it.unibo.jakta.agents.bdi.impl

import it.unibo.jakta.agents.bdi.Agent
import it.unibo.jakta.agents.bdi.AgentLifecycle
import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.actions.ExternalRequest
import it.unibo.jakta.agents.bdi.actions.InternalAction
import it.unibo.jakta.agents.bdi.actions.InternalRequest
import it.unibo.jakta.agents.bdi.actions.effects.AgentChange
import it.unibo.jakta.agents.bdi.actions.effects.BeliefChange
import it.unibo.jakta.agents.bdi.actions.effects.EnvironmentChange
import it.unibo.jakta.agents.bdi.actions.effects.EventChange
import it.unibo.jakta.agents.bdi.actions.effects.IntentionChange
import it.unibo.jakta.agents.bdi.actions.effects.Pause
import it.unibo.jakta.agents.bdi.actions.effects.PlanChange
import it.unibo.jakta.agents.bdi.actions.effects.PopMessage
import it.unibo.jakta.agents.bdi.actions.effects.Sleep
import it.unibo.jakta.agents.bdi.actions.effects.Stop
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.beliefs.BeliefUpdate
import it.unibo.jakta.agents.bdi.beliefs.RetrieveResult
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.context.ContextUpdate.ADDITION
import it.unibo.jakta.agents.bdi.context.ContextUpdate.REMOVAL
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.events.AchievementGoalFailure
import it.unibo.jakta.agents.bdi.events.BeliefBaseAddition
import it.unibo.jakta.agents.bdi.events.BeliefBaseRemoval
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.EventQueue
import it.unibo.jakta.agents.bdi.events.TestGoalFailure
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.goals.Achieve
import it.unibo.jakta.agents.bdi.goals.Act
import it.unibo.jakta.agents.bdi.goals.ActExternally
import it.unibo.jakta.agents.bdi.goals.ActInternally
import it.unibo.jakta.agents.bdi.goals.ActionGoal
import it.unibo.jakta.agents.bdi.goals.AddBelief
import it.unibo.jakta.agents.bdi.goals.BeliefGoal
import it.unibo.jakta.agents.bdi.goals.EmptyGoal
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.PlanExecutionTrackingGoal
import it.unibo.jakta.agents.bdi.goals.PlanGenerationStepGoal
import it.unibo.jakta.agents.bdi.goals.RemoveBelief
import it.unibo.jakta.agents.bdi.goals.Spawn
import it.unibo.jakta.agents.bdi.goals.Test
import it.unibo.jakta.agents.bdi.goals.UpdateBelief
import it.unibo.jakta.agents.bdi.intentions.GoalTrackingIntention
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.intentions.IntentionPool
import it.unibo.jakta.agents.bdi.logging.events.ActionFinished
import it.unibo.jakta.agents.bdi.logging.events.AssignPlanToExistingIntention
import it.unibo.jakta.agents.bdi.logging.events.AssignPlanToNewIntention
import it.unibo.jakta.agents.bdi.logging.events.EventSelected
import it.unibo.jakta.agents.bdi.logging.events.GoalAchieved
import it.unibo.jakta.agents.bdi.logging.events.GoalCreated
import it.unibo.jakta.agents.bdi.logging.events.GoalFailed
import it.unibo.jakta.agents.bdi.logging.events.IntentionGoalRun
import it.unibo.jakta.agents.bdi.logging.events.NewMessage
import it.unibo.jakta.agents.bdi.logging.events.NewPercept
import it.unibo.jakta.agents.bdi.logging.events.PlanCompleted
import it.unibo.jakta.agents.bdi.logging.events.PlanGenerationStepCompleted
import it.unibo.jakta.agents.bdi.logging.events.PlanSelected
import it.unibo.jakta.agents.bdi.logging.implementation
import it.unibo.jakta.agents.bdi.messages.Tell
import it.unibo.jakta.agents.bdi.plans.ActivationRecord
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.jakta.agents.bdi.plans.feedback.FeedbackProvider.provideFeedback
import it.unibo.jakta.agents.bdi.plans.feedback.GenerationFeedback
import it.unibo.jakta.agents.fsm.Activity

internal data class AgentLifecycleImpl(
    private var agent: Agent,
) : AgentLifecycle {
    private var cycleCount = 0
    private var controller: Activity.Controller? = null
    private var cachedEffects = emptyList<EnvironmentChange>()

    override fun updateBelief(perceptions: BeliefBase, beliefBase: BeliefBase): RetrieveResult =
        when (perceptions == beliefBase) {
            false -> {
                // 1. each literal l in p not currently in b is added to b
                val rrAddition = beliefBase.addAll(perceptions)

                // 2. each literal l in b no longer in p is deleted from b
                var removedBeliefs = emptyList<BeliefUpdate>()
                var rrRemoval = RetrieveResult(removedBeliefs, rrAddition.updatedBeliefBase)
                rrRemoval.updatedBeliefBase.forEach {
                    if (!perceptions.contains(it) && it.rule.head.args.first() == Belief.SOURCE_PERCEPT) {
                        rrRemoval = rrRemoval.updatedBeliefBase.remove(it)
                        removedBeliefs = removedBeliefs + rrRemoval.modifiedBeliefs
                    }
                }

                RetrieveResult(
                    rrAddition.modifiedBeliefs + rrRemoval.modifiedBeliefs,
                    rrRemoval.updatedBeliefBase,
                )
            }
            else -> RetrieveResult(emptyList(), beliefBase)
        }

    override fun selectEvent(events: EventQueue) = agent.selectEvent(events)

    override fun selectRelevantPlans(event: Event, planLibrary: PlanLibrary) = planLibrary.relevantPlans(event)

    override fun isPlanApplicable(event: Event, plan: Plan, beliefBase: BeliefBase) =
        plan.isApplicable(event, beliefBase)

    override fun selectApplicablePlan(plans: Iterable<Plan>) = agent.selectApplicablePlan(plans)

    override fun assignPlanToIntention(event: Event, plan: Plan, intentions: IntentionPool): Intention {
        val planActivationRecord = plan.toActivationRecord()
        if (planActivationRecord.goalQueue.isEmpty()) {
            agent.logger?.error { "[PlanToIntention] A plan must have at least a goal" }
        }
        val newIntention = when (event.isExternal()) {
            true -> Intention.of(recordStack = listOf(planActivationRecord)).also {
                agent.logger?.implementation(AssignPlanToNewIntention(it))
            }

            false ->
                when (event.trigger) {
                    is AchievementGoalFailure, is TestGoalFailure ->
                        event.intention!!.copy(recordStack = listOf(planActivationRecord))
//                     else -> intentions[event.intention!!.id]!!.push(plan.toActivationRecord())
                    else -> event.intention!!.pop().push(planActivationRecord)
                }.also {
                    agent.logger?.implementation(AssignPlanToExistingIntention(it))
                }
        }
        return newIntention
    }

    override fun scheduleIntention(intentions: IntentionPool) = agent.scheduleIntention(intentions)

    private fun executeInternalAction(
        intention: Intention,
        action: InternalAction,
        context: AgentContext,
        goal: ActionGoal,
    ): ExecutionResult {
        var newIntention = intention.pop()
        if (action.signature.arity < goal.action.args.size) {
            agent.logger?.warn { "[ArgNumberMismatch] Failed to invoke action" }
            return ExecutionResult(failAchievementGoal(intention, context))
        } else {
            val internalResponse = action.execute(
                InternalRequest.of(this.agent, controller?.currentTime(), goal.action.args),
            )
            agent.logger?.implementation(ActionFinished(action))
            // Apply substitution
            return if (internalResponse.substitution.isSuccess) {
                if (newIntention.recordStack.isNotEmpty()) {
                    newIntention = newIntention.applySubstitution(internalResponse.substitution)
                }
                val newContext = applyEffects(context, internalResponse.effects)
                agent.logger?.implementation(GoalAchieved(goal.value, intention.currentPlan()))
                if (intention.recordStack.first().isLastGoal()) {
                    agent.logger?.implementation(PlanCompleted(intention.currentPlan()))
                }
                ExecutionResult(
                    newContext.copy(intentions = newContext.intentions.updateIntention(newIntention)),
                )
            } else {
                ExecutionResult(failAchievementGoal(intention, context))
            }
        }
    }

    private fun executeExternalAction(
        intention: Intention,
        action: ExternalAction,
        context: AgentContext,
        environment: Environment,
        goal: ActionGoal,
    ): ExecutionResult {
        var newIntention = intention.pop()
        if (action.signature.arity < goal.action.args.size) {
            agent.logger?.warn { "[ArgNumberMismatch] Failed to invoke action" }
            return ExecutionResult(failAchievementGoal(intention, context))
        } else {
            val externalResponse = action.execute(
                ExternalRequest.of(
                    environment,
                    agent.name,
                    controller?.currentTime(),
                    goal.action.args,
                ),
            )
            agent.logger?.implementation(ActionFinished(action))
            return if (externalResponse.substitution.isSuccess) {
                if (newIntention.recordStack.isNotEmpty()) {
                    newIntention = newIntention.applySubstitution(externalResponse.substitution)
                }
                agent.logger?.implementation(GoalAchieved(goal.value, intention.currentPlan()))
                if (intention.recordStack.first().isLastGoal()) {
                    agent.logger?.implementation(PlanCompleted(intention.currentPlan()))
                }
                ExecutionResult(
                    context.copy(intentions = context.intentions.updateIntention(newIntention)),
                    externalResponse.effects,
                )
            } else {
                ExecutionResult(failAchievementGoal(intention, context))
            }
        }
    }

    override fun runIntention(
        intention: Intention,
        context: AgentContext,
        environment: Environment,
    ): ExecutionResult {
        agent.logger?.implementation(IntentionGoalRun(intention))
        return when (val nextGoal = intention.nextGoal()) {
            is EmptyGoal -> {
                val newIntention = intention.pop()
                ExecutionResult(
                    context.copy(intentions = context.intentions.updateIntention(newIntention)),
                ).also {
                    if (intention.recordStack.first().isLastGoal()) {
                        agent.logger?.implementation(PlanCompleted(intention.currentPlan()))
                    }
                    agent.logger?.implementation(GoalAchieved(nextGoal.value, intention.currentPlan()))
                }
            }

            is ActionGoal -> when (nextGoal) {
                is ActInternally -> {
                    val internalAction = context.internalActions[nextGoal.action.functor]

                    if (internalAction == null) {
                        // Internal Action not found
                        agent.logger?.warn { "${nextGoal.action.functor} Internal Action not found." }
                        ExecutionResult(failAchievementGoal(intention, context))
                    } else {
                        // Execute Internal Action
                        executeInternalAction(intention, internalAction, context, nextGoal)
                    }
                }
                is ActExternally -> {
                    val externalAction = environment.externalActions[nextGoal.action.functor]
                    if (externalAction == null) {
                        // Internal Action not found
                        agent.logger?.warn { "${nextGoal.action.functor} External Action not found." }
                        ExecutionResult(failAchievementGoal(intention, context))
                    } else {
                        // Execute External Action
                        executeExternalAction(intention, externalAction, context, environment, nextGoal)
                    }
                }
                is Act -> {
                    val action = (environment.externalActions + context.internalActions)[nextGoal.action.functor]
                    if (action == null) {
                        agent.logger?.warn { "${nextGoal.action.functor} Action not found." }
                        ExecutionResult(failAchievementGoal(intention, context))
                    } else {
                        // Execute Action
                        when (action) {
                            is InternalAction -> executeInternalAction(intention, action, context, nextGoal)
                            is ExternalAction ->
                                executeExternalAction(intention, action, context, environment, nextGoal)
                            else ->
                                throw IllegalStateException("The Action to execute is neither internal nor external")
                        }
                    }
                }
            }

            is Spawn -> {
                val newIntention = intention.pop()
                ExecutionResult(
                    context.copy(
                        events = context.events + Event.ofAchievementGoalInvocation(Achieve.of(nextGoal.value)),
                        intentions = context.intentions.updateIntention(newIntention),
                    ),
                ).also {
                    agent.logger?.implementation(GoalAchieved(nextGoal.value, intention.currentPlan()))
                }
            }

            is Achieve -> {
                val newEvent = if (intention is GoalTrackingIntention) {
                    val intentionWithTrackingGoal = intention.addTrackingGoal()
                    Event.ofAchievementGoalInvocation(nextGoal, intentionWithTrackingGoal)
                } else {
                    Event.ofAchievementGoalInvocation(nextGoal, intention)
                }
                ExecutionResult(
                    context.copy(
                        events = context.events + newEvent,
                        intentions = IntentionPool.of(context.intentions - intention.id),
                    ),
                ).also {
                    agent.logger?.implementation(EventChange(newEvent, ADDITION))
                }
            }

            is Test -> {
                val solution = context.beliefBase.solve(nextGoal.value)
                when (solution.isYes) {
                    true -> {
                        val newIntention = intention.pop().applySubstitution(solution.substitution)
                        agent.logger?.implementation(IntentionChange(newIntention, ADDITION))
                        ExecutionResult(
                            context.copy(
                                intentions = context.intentions.updateIntention(newIntention),
                            ),
                        )
                    }
                    else -> {
                        val failedGoal = intention.currentPlan().trigger
                        val newEvent = Event.ofTestGoalFailure(failedGoal.value, intention)
                        agent.logger?.implementation(EventChange(newEvent, ADDITION))
                        agent.logger?.implementation(GoalFailed(failedGoal.value))
                        ExecutionResult(
                            context.copy(
                                events = context.events + newEvent,
                            ),
                        )
                    }
                }
            }

            is BeliefGoal -> {
                val retrieveResult = when (nextGoal) {
                    is AddBelief -> context.beliefBase.add(Belief.from(nextGoal.value))
                    is RemoveBelief -> context.beliefBase.remove(Belief.from(nextGoal.value))
                    is UpdateBelief -> context.beliefBase.update(Belief.from(nextGoal.value))
                }
                val newIntention = intention.pop()
                ExecutionResult(
                    context.copy(
                        beliefBase = retrieveResult.updatedBeliefBase,
                        events = generateEvents(context.events, retrieveResult.modifiedBeliefs),
                        intentions = context.intentions.updateIntention(newIntention),
                    ),
                ).also {
                    agent.logger?.implementation(GoalAchieved(nextGoal.value, intention.currentPlan()))
                    if (intention.recordStack.first().isLastGoal()) {
                        agent.logger?.implementation(PlanCompleted(intention.currentPlan()))
                    }
                }
            }

            is PlanGenerationStepGoal -> handlePlanGenerationStep(nextGoal, intention, context, environment, agent)

            is PlanExecutionTrackingGoal -> handlePlanExecutionTracking(intention, context, agent)
        }
    }

    private fun handlePlanGenerationStep(
        genGoal: PlanGenerationStepGoal,
        intention: Intention,
        context: AgentContext,
        environment: Environment,
        agent: Agent,
    ): ExecutionResult {
        agent.logger?.implementation(PlanGenerationStepCompleted(genGoal.value, intention.currentPlan()))

        val goalToExecute = genGoal.goal
        val newGoalQueue = intention.recordStack.first().goalQueue - genGoal + goalToExecute
        val newIntention = intention.updateGoalQueueOfCurrentRecord(newGoalQueue)

        return if (goalToExecute.isNotBranching()) {
            handleNonBranchingGoal(newIntention, context, environment)
        } else {
            handleBranchingGoal(newIntention, context, environment, genGoal)
        }
    }

    private fun handleNonBranchingGoal(
        newIntention: Intention,
        context: AgentContext,
        environment: Environment,
    ): ExecutionResult {
        val res = runIntention(newIntention, context, environment)

        val newEvent = Event.ofAchievementGoalInvocation(
            Achieve.of(newIntention.currentPlan().trigger.value),
            newIntention,
        )

        val feedback = GenerationFeedback.of(newIntention.recordStack.first().goalQueue.first())
        val newPlanLibrary = provideFeedback(
            newIntention,
            feedback,
            res.newAgentContext.planLibrary,
        )
        val updatedPlanLibrary = updateGeneratedPlanIfNeeded(
            newIntention.currentPlan(),
            newPlanLibrary,
        )

        return ExecutionResult(
            res.newAgentContext.copy(
                events = res.newAgentContext.events + newEvent,
                planLibrary = updatedPlanLibrary,
            ),
            res.environmentEffects,
        )
    }

    private fun handleBranchingGoal(
        newIntention: Intention,
        context: AgentContext,
        environment: Environment,
        genGoal: PlanGenerationStepGoal,
    ): ExecutionResult {
        val trackingIntention = newIntention as? GoalTrackingIntention
            ?: GoalTrackingIntention.fromIntention(newIntention)
        val generatedPlanStack = trackingIntention.generatedPlanStack
        val finalIntention = if (!generatedPlanStack.contains(genGoal.planID)) {
            trackingIntention.copy(
                generatedPlanStack = generatedPlanStack + genGoal.planID,
            )
        } else {
            trackingIntention
        }

        val updatedPlanLibrary = updateGeneratedPlanIfNeeded(
            finalIntention.currentPlan(),
            context.planLibrary,
        )

        return runIntention(finalIntention, context.copy(planLibrary = updatedPlanLibrary), environment)
    }

    private fun handlePlanExecutionTracking(
        intention: Intention,
        context: AgentContext,
        agent: Agent,
    ): ExecutionResult {
        /*
         * If this goal is not the last of the record stack, just drop it.
         * Since the goal is meant to track when a plan completes, it
         * has a purpose only if it is in the last position, to avoid
         * deleting the record stack of the plan being tracked.
         */
        if (intention.recordStack.first().isLastGoal()) {
            agent.logger?.implementation(PlanCompleted(intention.currentPlan()))

            if (intention is GoalTrackingIntention && intention.isCurrentPlanGenerated()) {
                return handleGeneratedPlanCompletion(intention, context, agent)
            }
        }

        return ExecutionResult(
            context.copy(
                intentions = context.intentions.updateIntention(intention.pop()),
            ),
        )
    }

    private fun handleGeneratedPlanCompletion(
        intention: GoalTrackingIntention,
        context: AgentContext,
        agent: Agent,
    ): ExecutionResult {
        val planID = intention.currentPlan()
        val feedback = GenerationFeedback.of(*intention.goalAchievedTrace.toTypedArray())
        val newPlanLibrary = provideFeedback(intention, feedback, agent.context.planLibrary)
        val updatedPlanLibrary = updateGeneratedPlanIfNeeded(planID, newPlanLibrary)

        val newIntention = if (intention.isCurrentPlanLastGeneratedOne()) {
            GoalTrackingIntention.toIntention(intention)
        } else {
            intention
        }

        val newGoal = Achieve.of(newIntention.currentPlan().trigger.value)
        val newEvent = Event.ofAchievementGoalInvocation(newGoal, newIntention)

        return ExecutionResult(
            context.copy(
                planLibrary = updatedPlanLibrary,
                intentions = context.intentions.updateIntention(newIntention),
                events = context.events + newEvent,
            ),
        )
    }

    private fun updateGeneratedPlanIfNeeded(
        planId: PlanID,
        planLibrary: PlanLibrary,
    ): PlanLibrary {
        val genPlan = planLibrary.plans.firstOrNull { it.id == planId } as? GeneratedPlan ?: return planLibrary

        val filteredGoals = genPlan.goals.map {
            if (it is PlanGenerationStepGoal) it.goal else it
        }

        val planWithFeedback = GeneratedPlan.of(
            id = genPlan.id,
            trigger = genPlan.trigger,
            goals = filteredGoals,
            guard = genPlan.guard,
            generationStrategy = genPlan.generationStrategy,
            literateTrigger = genPlan.literateTrigger,
            literateGuard = genPlan.literateGuard,
            literateGoals = genPlan.literateGoals,
        )

        return PlanLibrary.of(planLibrary.plans)
            .removePlan(genPlan)
            .addPlan(planWithFeedback)
    }

    private fun applyEffects(context: AgentContext, effects: Iterable<AgentChange>): AgentContext {
        var newBeliefBase = context.beliefBase
        var newEvents = context.events
        var newPlans = context.planLibrary
        var newIntentions = context.intentions
        effects.forEach {
            agent.logger?.implementation(it)
            when (it) {
                is BeliefChange -> {
                    val rr = when (it.changeType) {
                        ADDITION -> newBeliefBase.add(it.belief)
                        REMOVAL -> newBeliefBase.remove(it.belief)
                    }
                    newBeliefBase = rr.updatedBeliefBase
                    newEvents = generateEvents(newEvents, rr.modifiedBeliefs)
                }
                is IntentionChange -> newIntentions = when (it.changeType) {
                    ADDITION -> newIntentions.updateIntention(it.intention)
                    REMOVAL -> newIntentions.deleteIntention(it.intention.id)
                }
                is EventChange -> newEvents = when (it.changeType) {
                    ADDITION -> newEvents + it.event
                    REMOVAL -> newEvents - it.event
                }
                is PlanChange -> newPlans = when (it.changeType) {
                    ADDITION -> newPlans.addPlan(it.plan)
                    REMOVAL -> newPlans.removePlan(it.plan)
                }

                is Pause -> controller?.pause()
                is Sleep -> controller?.sleep(it.millis)
                is Stop -> controller?.stop()
            }
        }
        return context.copy(
            beliefBase = newBeliefBase,
            events = newEvents,
            planLibrary = newPlans,
            intentions = newIntentions,
        )
    }

    private fun failAchievementGoal(intention: Intention, context: AgentContext): AgentContext {
        val failedGoal = intention.currentPlan()
        val newEvent = Event.ofAchievementGoalFailure(failedGoal.trigger.value, intention)
        agent.logger?.implementation(EventChange(newEvent, ADDITION))
        agent.logger?.implementation(GoalFailed(failedGoal.trigger.value))
        return context.copy(
            events = context.events + newEvent,
        )
    }

    private fun generateEvents(events: EventQueue, modifiedBeliefs: List<BeliefUpdate>): EventQueue =
        events + modifiedBeliefs.map {
            when (it.updateType) {
                REMOVAL -> Event.of(BeliefBaseRemoval(it.belief))
                ADDITION -> Event.of(BeliefBaseAddition(it.belief))
            }.also { ev ->
                agent.logger?.implementation(EventChange(ev, it.updateType))
            }
        }

    override fun sense(environment: Environment, controller: Activity.Controller?) {
        this.controller = controller

        // STEP1: Perceive the Environment
        val perceptions = environment.percept()
        perceptions.forEach {
            agent.logger?.implementation(
                NewPercept(
                    percept = it,
                    source = "environment",
                ),
            )
        }

        // STEP2: Update the BeliefBase
        val rr = updateBelief(perceptions, agent.context.beliefBase)
        rr.modifiedBeliefs.forEach {
            agent.logger?.implementation(BeliefChange(it.belief, it.updateType))
        }

        var newBeliefBase = rr.updatedBeliefBase
        agent.logger?.trace { "pre-run -> ${agent.context}" }
        // Generate events related to BeliefBase revision
        var newEvents = generateEvents(agent.context.events, rr.modifiedBeliefs)

        // STEP3: Receiving Communication from Other Agents
        val message = environment.getNextMessage(agent.name)

        // STEP4: Selecting "Socially Acceptable" Messages //TODO()

        // Parse message
        if (message != null) {
            agent.logger?.implementation(NewMessage(message = message))
            newEvents = when (message.type) {
                is it.unibo.jakta.agents.bdi.messages.Achieve -> {
                    val goalToAchieve = Achieve.of(message.value)
                    agent.logger?.implementation(GoalCreated(goalToAchieve))
                    newEvents + Event.ofAchievementGoalInvocation(goalToAchieve)
                }
                is Tell -> {
                    val beliefFromMessage = Belief.fromMessageSource(message.from, message.value)
                    agent.logger?.implementation(BeliefChange(beliefFromMessage, ADDITION))
                    val retrieveResult = newBeliefBase.add(beliefFromMessage)
                    newBeliefBase = retrieveResult.updatedBeliefBase
                    generateEvents(newEvents, retrieveResult.modifiedBeliefs)
                }
            }
            cachedEffects = cachedEffects + PopMessage(this.agent.name)
        }
        this.agent = this.agent.copy(beliefBase = newBeliefBase, events = newEvents)
    }

    private fun getRelevantPlansWithFeedback(
        intention: GoalTrackingIntention,
    ): PlanLibrary {
        val feedback = GenerationFeedback.of(
            "No relevant plan found for the given goal " +
                formatter.format(intention.currentPlan().trigger.value),
        )
        return provideFeedback(intention, feedback, agent.context.planLibrary)
    }

    private fun getApplicablePlansWithFeedback(
        intention: GoalTrackingIntention,
        relevantPlans: PlanLibrary,
        selectedEvent: Event,
    ): PlanLibrary {
        val plans = relevantPlans.plans.map {
            it.checkApplicability(selectedEvent, this.agent.context.beliefBase)
        }
        val feedback = GenerationFeedback.of(plans)
        return provideFeedback(intention, feedback, agent.context.planLibrary)
    }

    override fun deliberate(environment: Environment) {
        // STEP5: Selecting an Event.
        var newPlanLibrary = this.agent.context.planLibrary
        var newEvents = this.agent.context.events
        var newIntentionPool = agent.context.intentions
        val selectedEvent = selectEvent(this.agent.context.events)

        if (selectedEvent != null) {
            agent.logger?.implementation(EventSelected(selectedEvent))
            newEvents = newEvents - selectedEvent

            // STEP6: Retrieving all Relevant Plans.
            val relevantPlans = selectRelevantPlans(selectedEvent, agent.context.planLibrary)

            // STEP7: Determining the Applicable Plans.
            val applicablePlans = relevantPlans.plans.filter {
                isPlanApplicable(selectedEvent, it, this.agent.context.beliefBase)
            }

            // STEP8: Selecting one Applicable Plan.
            val candidateSelectedPlan = selectApplicablePlan(applicablePlans)

            // Provide feedback on relevance and applicability if the generation of a plan is running
            val intention = selectedEvent.intention
            if (intention != null && intention is GoalTrackingIntention) {
                val relevantPlansWithFeedback = if (relevantPlans.plans.isEmpty()) {
                    getRelevantPlansWithFeedback(intention)
                } else {
                    relevantPlans
                }

                if (applicablePlans.isEmpty()) {
                    val applicablePlansWithFeedback = getApplicablePlansWithFeedback(
                        intention,
                        relevantPlansWithFeedback,
                        selectedEvent,
                    )
                    newPlanLibrary = applicablePlansWithFeedback

                    val newIntention = if (intention.isCurrentPlanLastGeneratedOne()) {
                        GoalTrackingIntention.toIntention(intention)
                    } else {
                        intention
                    }

                    val newGoal = Achieve.of(intention.currentPlan().trigger.value)
                    val newGenerationEvent = Event.ofAchievementGoalInvocation(newGoal, newIntention)
                    newEvents = newEvents + newGenerationEvent
                }
            }

            // Check if the plan uses a generation strategy and apply it.
            val selectedPlan: Plan? =
                if (candidateSelectedPlan is GeneratedPlan) {
                    val generationStrategy = candidateSelectedPlan.generationStrategy
                    if (generationStrategy != null) {
                        val planGenResult = generationStrategy.requestPlanGeneration(
                            candidateSelectedPlan,
                            agent.context,
                            environment.externalActions.values.toList(),
                        )
                        val generatedPlan = planGenResult.generatedPlan
                        if (generatedPlan != null && planGenResult.errorMsg == null) {
                            // Update the GeneratedPlan with the newly generated goal
                            newPlanLibrary = newPlanLibrary
                                .removePlan(candidateSelectedPlan)
                                .addPlan(generatedPlan)

                            // TODO what if the generation terminates and the plan does not
                            // satisfy the goal?
                            // When a Plan is returned check if this happens
                            generatedPlan as? GeneratedPlan
                        } else {
                            agent.logger?.error { "Failed generation due to: ${planGenResult.errorMsg}" }
                            if (planGenResult.trials < planGenResult.maxTrials &&
                                candidateSelectedPlan.goals.isNotEmpty()
                            ) {
                                candidateSelectedPlan
                            } else {
                                null
                            }
                        }
                    } else {
                        agent.logger?.warn { "Cannot generate new goals without a generation strategy" }
                        if (candidateSelectedPlan.goals.isNotEmpty()) {
                            candidateSelectedPlan
                        } else {
                            null
                        }
                    }
                } else {
                    candidateSelectedPlan
                }

            // Add plan to intentions
            if (selectedPlan != null) {
                agent.logger?.implementation(PlanSelected(selectedPlan))
                val updatedIntention = assignPlanToIntention(
                    selectedEvent,
                    selectedPlan.applicablePlan(selectedEvent, this.agent.context.beliefBase),
                    agent.context.intentions,
                )
                newIntentionPool = agent.context.intentions.updateIntention(updatedIntention)
            } else {
                agent.logger?.info {
                    "There's no applicable plan for the event: ${formatter.format(
                        selectedEvent.trigger.value,
                    )}"
                }
                if (selectedEvent.isInternal()) {
                    val intentionToRemove = selectedEvent.intention!!
                    agent.logger?.implementation(IntentionChange(intentionToRemove, REMOVAL))
                    newIntentionPool = newIntentionPool.deleteIntention(intentionToRemove.id)
                }
            }
        }

        // Select intention to execute
        this.agent = this.agent.copy(
            events = newEvents,
            planLibrary = newPlanLibrary,
            intentions = newIntentionPool,
        )
    }

    override fun act(environment: Environment): Iterable<EnvironmentChange> {
        var executionResult = ExecutionResult(AgentContext.of())
        var newIntentionPool = agent.context.intentions
        if (!newIntentionPool.isEmpty()) {
            // STEP9: Select an Intention for Further Execution.
            val result = scheduleIntention(newIntentionPool)
            val scheduledIntention = result.intentionToExecute
            newIntentionPool = result.newIntentionPool
            // STEP10: Executing one Step on an Intention
            this.agent = if (scheduledIntention.recordStack.isEmpty()) {
                agent.logger?.implementation(IntentionChange(scheduledIntention, REMOVAL))
                this.agent.copy(intentions = newIntentionPool)
            } else {
                executionResult = runIntention(
                    scheduledIntention,
                    this.agent.context.copy(intentions = newIntentionPool),
                    environment,
                )
                // if the plan execution is successful, report it
                // adds a new event to keep generating and set generate to false
                this.agent.copy(executionResult.newAgentContext)
            }
            agent.logger?.trace { "post run -> ${agent.context}" }
        } else {
//            agent.logger?.warn { "This agent lacks any intention" }
        }

        // Generate Environment Changes
        val environmentChangesToApply = executionResult.environmentEffects + cachedEffects
        cachedEffects = emptyList()
        return environmentChangesToApply
    }

    override fun runOneCycle(
        environment: Environment,
        controller: Activity.Controller?,
    ): Iterable<EnvironmentChange> {
        cycleCount++
        sense(environment, controller)
        deliberate(environment)
        return act(environment)
    }

    companion object {
        fun Intention.addTrackingGoal(): Intention {
            val newTrackingGoal = PlanExecutionTrackingGoal.of(this.currentPlan())
            val newGoalQueue = this.recordStack.first().goalQueue + newTrackingGoal
            return this.updateGoalQueueOfCurrentRecord(newGoalQueue)
        }

        fun Intention.updateGoalQueueOfCurrentRecord(goalQueue: List<Goal>): Intention {
            val plan = this.currentPlan()
            val newActivationRecord = ActivationRecord.of(goalQueue, plan)
            val newRecordStack = listOf(newActivationRecord) + this.recordStack.drop(1)
            return this.copy(recordStack = newRecordStack)
        }

        fun Goal.isNotBranching() = this !is Achieve && this !is Test && this !is Spawn
    }
}
