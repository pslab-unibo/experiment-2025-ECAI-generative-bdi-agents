package it.unibo.jakta.playground.domesticrobot.environment

import it.unibo.jakta.agents.bdi.engine.AgentID
import it.unibo.jakta.agents.bdi.engine.Jakta.capitalize
import it.unibo.jakta.agents.bdi.engine.JaktaParser.tangleStruct
import it.unibo.jakta.agents.bdi.engine.actions.ExternalAction
import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.engine.environment.impl.EnvironmentImpl
import it.unibo.jakta.agents.bdi.engine.logging.loggers.MasLogger
import it.unibo.jakta.agents.bdi.engine.messages.MessageQueue
import it.unibo.jakta.agents.bdi.engine.perception.Perception
import it.unibo.tuprolog.core.Atom
import java.lang.Thread.sleep

class HouseEnvironment(
    agentIDs: Map<String, AgentID> = emptyMap(),
    externalActions: Map<String, ExternalAction> = emptyMap(),
    messageBoxes: Map<AgentID, MessageQueue> = emptyMap(),
    override var perception: Perception = Perception.empty(),
    data: Map<String, Any> = defaultData,
    override val logger: MasLogger? = null,
) : EnvironmentImpl(externalActions, agentIDs, messageBoxes, perception, data, logger) {
    private val perceptsFactory = HousePercepts()

    init {
        perception = Perception.of(getPercepts())
    }

    private fun getPercepts(): List<Belief> {
        val state = data.state()
        if (state != null) {
            val hasOwnerBeer = perceptsFactory.createHasOwnerBelief(state)
            val robotLocation = perceptsFactory.createRobotLocationBelief(state)
            val beerStock = perceptsFactory.createBeerStockBelief(state)
            return listOfNotNull(hasOwnerBeer, robotLocation, beerStock)
        } else {
            return emptyList()
        }
    }

    override fun updateData(newData: Map<String, Any>): HouseEnvironment =
        copy(data = newData).also {
            getPercepts().forEach { b -> logger?.info { b.purpose?.capitalize() } }
        }

    fun parseAction(actionName: String): HouseState? {
        val state = data.state() ?: return null
        return when (actionName) {
            "open(fridge)" ->
                state.openFridge().also {
                    logger?.info { "The fridge is open" }
                }

            "close(fridge)" ->
                state.closeFridge().also {
                    logger?.info { "The fridge is closed" }
                }

            "pick(beer)" ->
                state.getBeer()?.also {
                    logger?.info { "The robot is carrying the beer" }
                }

            "hand_in(beer)" ->
                state.handInBeer()?.also {
                    logger?.info { "The robot handed the beer to the owner" }
                }

            "sip(beer)" ->
                state.sipBeer()?.also {
                    logger?.info { "The owner took a sip of the beer" }
                }

            else -> {
                val action = tangleStruct(actionName)
                when (action?.functor) {
                    "move_towards" -> {
                        val destination = action.args[0] as? Atom
                        destination?.let { d ->
                            state.moveTowards(d.value)?.also { updatedModel ->
                                logger?.info {
                                    "Robot moved towards $destination," +
                                        " old position: ${state.robotPosition}," +
                                        " new position: ${updatedModel.robotPosition}"
                                }
                            }
                        } ?: state
                    }
                    "deliver" -> {
                        sleep(FAKE_DELIVERY_TIME_MS)
                        val numBeers = action.args[1].toString().toIntOrNull()
                        numBeers?.let {
                            state.addBeer(it).also { updatedModel ->
                                logger?.info { "Delivered $it beers" }
                            }
                        }
                    }
                    else -> {
                        state.also { logger?.warn { "Unknown action: $actionName" } }
                    }
                }
            }
        }
    }

    override fun percept(): BeliefBase {
        perception = Perception.of(getPercepts())
        return super.percept()
    }

    override fun copy(
        agentIDs: Map<String, AgentID>,
        externalActions: Map<String, ExternalAction>,
        messageBoxes: Map<AgentID, MessageQueue>,
        perception: Perception,
        data: Map<String, Any>,
        logger: MasLogger?,
    ): HouseEnvironment =
        HouseEnvironment(
            agentIDs,
            externalActions,
            messageBoxes,
            perception,
            data,
            logger,
        )

    companion object {
        const val FAKE_DELIVERY_TIME_MS = 200L

        internal fun Map<String, Any>.state() = this["state"] as? HouseState

        internal val defaultData = mapOf("state" to HouseState())
    }
}
