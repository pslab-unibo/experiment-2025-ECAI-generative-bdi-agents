package it.unibo.jakta.playground.explorer.gridworld

import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.beliefs.Belief.Companion.SOURCE_PERCEPT
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import kotlin.collections.iterator

class BeliefFactory {
    fun createObstacleBeliefs(grid: Grid, state: GridWorldState): List<Belief> {
        val result = mutableListOf<Belief>()

        for (direction in state.availableDirections.filter { it != Direction.HERE }) {
            val isObstacle = grid.isObstacleInDirection(state.agentPosition, direction)
            val obstacleBelief = if (isObstacle) {
                Struct.of("obstacle", Atom.of(direction.id))
            } else {
                Struct.of("not", Struct.of("obstacle", Atom.of(direction.id)))
            }
            result.add(Belief.wrap(obstacleBelief, wrappingTag = SOURCE_PERCEPT))
        }

        return result
    }

    fun createThereIsBeliefs(state: GridWorldState): List<Belief> {
        val result = mutableListOf<Belief>()

        for ((objectName, position) in state.objects) {
            val direction = state.agentPosition.directionTo(position)
            if (direction != null && (
                    state.agentPosition.isAdjacentTo(position) ||
                        state.agentPosition.isOn(position)
                    )
            ) {
                val struct = Struct.of("there_is", Atom.of(objectName), Atom.of(direction.id))
                result.add(Belief.wrap(struct, wrappingTag = SOURCE_PERCEPT))
            }
        }

        return result
    }
}
