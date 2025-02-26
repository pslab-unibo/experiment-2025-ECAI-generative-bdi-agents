package it.unibo.jakta.agents.bdi.impl

import it.unibo.jakta.agents.bdi.Agent
import it.unibo.jakta.agents.bdi.AgentLifecycle
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
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.goals.RemoveBelief
import it.unibo.jakta.agents.bdi.goals.Spawn
import it.unibo.jakta.agents.bdi.goals.Test
import it.unibo.jakta.agents.bdi.goals.UpdateBelief
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.intentions.IntentionPool
import it.unibo.jakta.agents.bdi.logging.events.ActionFinished
import it.unibo.jakta.agents.bdi.logging.events.AssignPlanToExistingIntention
import it.unibo.jakta.agents.bdi.logging.events.AssignPlanToNewIntention
import it.unibo.jakta.agents.bdi.logging.events.EventSelected
import it.unibo.jakta.agents.bdi.logging.events.GenerationEvent
import it.unibo.jakta.agents.bdi.logging.events.GoalAchieved
import it.unibo.jakta.agents.bdi.logging.events.GoalCreated
import it.unibo.jakta.agents.bdi.logging.events.GoalFailed
import it.unibo.jakta.agents.bdi.logging.events.IntentionGoalRun
import it.unibo.jakta.agents.bdi.logging.events.NewMessage
import it.unibo.jakta.agents.bdi.logging.events.NewPercept
import it.unibo.jakta.agents.bdi.logging.events.PlanSelected
import it.unibo.jakta.agents.bdi.logging.implementation
import it.unibo.jakta.agents.bdi.messages.Tell
import it.unibo.jakta.agents.bdi.plans.ActivationRecord
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
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

    override fun assignPlanToIntention(event: Event, plan: Plan, intentions: IntentionPool) =
        when (event.isExternal()) {
            true -> Intention.of(plan).also {
                agent.logger?.implementation(AssignPlanToNewIntention(it))
            }
            false -> {
                when (event.trigger) {
                    is AchievementGoalFailure, is TestGoalFailure ->
                        event.intention!!.copy(recordStack = listOf(plan.toActivationRecord()))
                    // else -> intentions[event.intention!!.id]!!.push(plan.toActivationRecord())
                    else -> event.intention!!.pop().push(plan.toActivationRecord())
                }.also {
                    agent.logger?.implementation(AssignPlanToExistingIntention(it))
                }
            }
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
                } else {
                    agent.logger?.warn { "[IntentionLacksGoal] Intention has no other goals to run" }
                    agent.logger?.implementation(GoalAchieved(intention.currentPlan()))
                }
                val newContext = applyEffects(context, internalResponse.effects)
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
                } else {
                    agent.logger?.warn { "[IntentionLacksGoal] Intention has no other goals to run" }
                    agent.logger?.implementation(GoalAchieved(intention.currentPlan()))
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
            is EmptyGoal -> ExecutionResult(
                context.copy(intentions = context.intentions.updateIntention(intention.pop())),
            )
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
            is Spawn -> ExecutionResult(
                context.copy(
                    events = context.events + Event.ofAchievementGoalInvocation(Achieve.of(nextGoal.value)),
                    intentions = context.intentions.updateIntention(intention.pop()),
                ),
            )
            is Achieve -> ExecutionResult(
                context.copy(
                    events = context.events + Event.ofAchievementGoalInvocation(nextGoal, intention),
                    intentions = IntentionPool.of(context.intentions - intention.id),
                ),
            )
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
                        val failedGoal = intention.currentPlan()
                        val newEvent = Event.ofTestGoalFailure(failedGoal, intention)
                        agent.logger?.implementation(EventChange(newEvent, ADDITION))
                        agent.logger?.implementation(GoalFailed(failedGoal))
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
                ExecutionResult(
                    context.copy(
                        beliefBase = retrieveResult.updatedBeliefBase,
                        events = generateEvents(context.events, retrieveResult.modifiedBeliefs),
                        intentions = context.intentions.updateIntention(intention.pop()),
                    ),
                )
            }
            is Generate -> {
                // Will return an empty intention since it has only one action
                val updatedGoalQueue = intention.recordStack.first().goalQueue - nextGoal + nextGoal.goal
                val plan = intention.currentPlan()
                val updatedRecordStack = intention.recordStack.drop(1) + ActivationRecord.of(updatedGoalQueue, plan)
                val execRes = runIntention(intention.copy(recordStack = updatedRecordStack), context, environment)
                // TODO update the GeneratedPlan with a message that gives feedback on the executed action
                // TODO convert the Generate to a normal goal once it is executed successfully
                execRes
            }
        }
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
        val newEvent = Event.ofAchievementGoalFailure(failedGoal, intention)
        agent.logger?.implementation(EventChange(newEvent, ADDITION))
        agent.logger?.implementation(GoalFailed(failedGoal))
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

    override fun deliberate(environment: Environment) {
        // STEP5: Selecting an Event.
        var newEvents = this.agent.context.events
        val selectedEvent = selectEvent(this.agent.context.events)
        var newIntentionPool = agent.context.intentions
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

            // Check if the plan uses a generation strategy and apply it.
            val selectedPlan: Plan? =
                if (candidateSelectedPlan is GeneratedPlan) {
                    val genStrategy = candidateSelectedPlan.generationStrategy
                    if (genStrategy != null) {
                        val planGenResult = genStrategy.requestPlanGeneration(
                            candidateSelectedPlan,
                            agent.context,
                            environment.externalActions.values.toList(),
                        )
                        val generatedPlan = planGenResult.generatedPlan
                        if (generatedPlan != null && planGenResult.errorMsg == null) {
                            /*
                                Update the [GeneratedPlan] with the newly generated goal
                             */
                            agent.context.planLibrary.removePlan(candidateSelectedPlan)
                            agent.context.planLibrary.addPlan(generatedPlan)

                            /*
                                Until an instance of a [Plan] is returned, the generation process is not ended
                                and a new event is added to keep it going.

                                The newly added event is always external.
                             */
                            if (generatedPlan is GeneratedPlan) {
                                agent.logger?.implementation(GenerationEvent(generatedPlan))
                                newEvents = newEvents + Event.of(selectedEvent.trigger)
                            }
                            generatedPlan
                        } else {
                            agent.logger?.error { "Failed generation due to: ${planGenResult.errorMsg}" }
                            null
                        }
                    } else {
                        agent.logger?.error { "Failed generation due the missing strategy" }
                        null
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
                agent.logger?.warn { "There's no applicable plan for the event: $selectedEvent" }
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
                agent.logger?.warn { "[IntentionLacksGoal] Intention has no other goals to run" }
                this.agent.copy(intentions = newIntentionPool)
            } else {
                executionResult = runIntention(
                    scheduledIntention,
                    this.agent.context.copy(intentions = newIntentionPool),
                    environment,
                )
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
}
