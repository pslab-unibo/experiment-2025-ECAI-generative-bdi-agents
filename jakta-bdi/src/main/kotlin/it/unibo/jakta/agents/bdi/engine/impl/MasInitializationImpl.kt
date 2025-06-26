package it.unibo.jakta.agents.bdi.engine.impl

import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.Mas
import it.unibo.jakta.agents.bdi.engine.MasID
import it.unibo.jakta.agents.bdi.engine.MasInitialization
import it.unibo.jakta.agents.bdi.engine.actions.effects.AdmissibleBeliefChange
import it.unibo.jakta.agents.bdi.engine.actions.effects.AdmissibleGoalChange
import it.unibo.jakta.agents.bdi.engine.actions.effects.BeliefChange
import it.unibo.jakta.agents.bdi.engine.actions.effects.EventChange
import it.unibo.jakta.agents.bdi.engine.actions.effects.PlanChange
import it.unibo.jakta.agents.bdi.engine.actions.effects.SpawnAgent
import it.unibo.jakta.agents.bdi.engine.context.ContextUpdate.ADDITION
import it.unibo.jakta.agents.bdi.engine.depinjection.JaktaKoin
import it.unibo.jakta.agents.bdi.engine.depinjection.JaktaKoin.engineJsonModule
import it.unibo.jakta.agents.bdi.engine.generation.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.logging.events.ActionEvent.ActionAddition
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.logging.loggers.MasLogger

internal class MasInitializationImpl(
    override val mas: Mas,
) : MasInitialization {
    override fun initialize(): Mas =
        mas
            .let(::initializeKoin) // init before creating and using the loggers
            .let(::addMasLogger)
            .let(::initializeAgents)
            .let(::addAgentsToEnvironment)
            .let(::logInitialState)

    private fun addMasLogger(mas: Mas): Mas {
        val logger =
            mas.loggingConfig?.let {
                MasLogger.create(mas.masID, it)
            }
        return mas.copy(logger = logger)
    }

    private fun initializeAgents(mas: Mas): Mas =
        mas.copy(
            agents =
                mas.agents.map { agent ->
                    agent
                        .assignMas(mas.masID)
                        .assignLogger(mas.loggingConfig)
                        .assignGenerationStrategy(mas.generationStrategy)
                },
        )

    private fun addAgentsToEnvironment(mas: Mas): Mas {
        val updatedEnvironment =
            mas.agents
                .fold(mas.environment) { env, agent ->
                    env.addAgent(agent)
                }.copy(logger = mas.logger)

        return mas.copy(environment = updatedEnvironment)
    }

    private fun initializeKoin(mas: Mas): Mas =
        mas.also {
            JaktaKoin.loadAdditionalModules(
                engineJsonModule,
                *mas.modules.toTypedArray(),
            )
        }

    private fun logInitialState(mas: Mas): Mas {
        mas.environment.externalActions.values.forEach { action ->
            mas.logger?.log { ActionAddition(action) }
        }

        mas.agents.forEach { agent ->
            mas.logger?.log { SpawnAgent(agent) }
            logAgentState(agent)
        }
        return mas
    }

    private fun logAgentState(agent: Agent) {
        agent.context.admissibleGoals.forEach { goal ->
            agent.logger?.log { AdmissibleGoalChange(goal, ADDITION) }
        }
        agent.context.admissibleBeliefs.forEach { belief ->
            agent.logger?.log { AdmissibleBeliefChange(belief, ADDITION) }
        }
        agent.context.events.forEach { event ->
            agent.logger?.log { EventChange(event, ADDITION) }
        }
        agent.context.planLibrary.plans.forEach { plan ->
            agent.logger?.log { PlanChange(plan, ADDITION) }
        }
        agent.context.beliefBase.forEach { belief ->
            agent.logger?.log { BeliefChange(belief, ADDITION) }
        }
        agent.context.internalActions.values.forEach { action ->
            agent.logger?.log { ActionAddition(action) }
        }
    }

    companion object {
        private fun Agent.assignMas(masId: MasID): Agent = copy(masID = masId)

        private fun Agent.assignLogger(loggingConfig: LoggingConfig?): Agent {
            val logger =
                loggingConfig?.let { cfg ->
                    masID?.let { AgentLogger.create(masID!!, agentID, cfg) }
                }
            return this.copy(loggingConfig = loggingConfig, logger = logger)
        }

        private fun Agent.assignGenerationStrategy(masGenerationStrategy: GenerationStrategy?): Agent =
            if (this.generationStrategy == null && masGenerationStrategy != null) {
                this.copy(generationStrategy = masGenerationStrategy)
            } else {
                this
            }
    }
}
