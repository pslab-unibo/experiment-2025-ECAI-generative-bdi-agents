package it.unibo.jakta.agents.bdi.impl

import it.unibo.jakta.agents.bdi.Agent
import it.unibo.jakta.agents.bdi.Mas
import it.unibo.jakta.agents.bdi.MasInitialization
import it.unibo.jakta.agents.bdi.actions.effects.BeliefChange
import it.unibo.jakta.agents.bdi.actions.effects.EventChange
import it.unibo.jakta.agents.bdi.actions.effects.PlanChange
import it.unibo.jakta.agents.bdi.context.ContextUpdate.ADDITION
import it.unibo.jakta.agents.bdi.logging.LoggerFactory.createLogger
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.logging.events.ActionEvent.ActionAddition
import it.unibo.jakta.agents.bdi.logging.implementation
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.jakta.agents.bdi.plans.copy

class MasInitializationImpl(override val mas: Mas) : MasInitialization {
    override fun initialize(): Mas =
        mas
            .let(::initializeAgents)
            .let(::addAgentsToEnvironment)
            .let(::logInitialState)

    private fun initializeAgents(mas: Mas): Mas =
        mas.copy(
            agents = mas.agents.map { agent ->
                val agentLoggingConfig = mas.loggingConfig?.copy(
                    logDir = "${mas.loggingConfig!!.logDir}/${agent.name}",
                )
                agent
                    .assignLogger(agentLoggingConfig)
                    .assignGenerationStrategy(mas.generationStrategy)
                    .assignGenerationStrategyToPlans()
            },
        )

    private fun addAgentsToEnvironment(mas: Mas): Mas {
        val updatedEnvironment = mas.agents.fold(mas.environment) { env, agent ->
            env.addAgent(agent)
        }
        return mas.copy(environment = updatedEnvironment)
    }

    private fun logInitialState(mas: Mas): Mas {
        mas.environment.externalActions.values.forEach { action ->
            mas.logger?.implementation(ActionAddition(action))
        }

        mas.agents.forEach { logAgentState(it) }
        return mas
    }

    private fun logAgentState(agent: Agent) {
        agent.context.events.forEach { event ->
            agent.logger?.implementation(EventChange(event, ADDITION))
        }
        agent.context.planLibrary.plans.forEach { plan ->
            agent.logger?.implementation(PlanChange(plan, ADDITION))
        }
        agent.context.beliefBase.forEach { belief ->
            agent.logger?.implementation(BeliefChange(belief, ADDITION))
        }
        agent.context.internalActions.values.forEach { action ->
            agent.logger?.implementation(ActionAddition(action))
        }
    }

    companion object {
        private fun Agent.assignLogger(loggingConfig: LoggingConfig?): Agent {
            val logger = loggingConfig?.let { createLogger(it, this.name) }
            return this.copy(loggingConfig = loggingConfig, logger = logger)
        }

        private fun Agent.assignGenerationStrategy(
            masGenerationStrategy: GenerationStrategy?,
        ): Agent {
            return if (this.generationStrategy == null && masGenerationStrategy != null) {
                this.copy(generationStrategy = masGenerationStrategy)
            } else {
                this
            }
        }

        private fun Agent.assignGenerationStrategyToPlans(): Agent {
            val agentGenerationStrategy = this.generationStrategy
            val updatedPlans = this.context.planLibrary.plans.map { plan ->
                plan.assignGenerationStrategy(agentGenerationStrategy)
            }
            return this.copy(planLibrary = PlanLibrary.of(updatedPlans))
        }

        private fun Plan.assignGenerationStrategy(
            agentGenerationStrategy: GenerationStrategy?,
        ): Plan {
            return if (this is PartialPlan && agentGenerationStrategy != null) {
                this.copy(generationStrategy = agentGenerationStrategy)
            } else {
                this
            }
        }
    }
}
