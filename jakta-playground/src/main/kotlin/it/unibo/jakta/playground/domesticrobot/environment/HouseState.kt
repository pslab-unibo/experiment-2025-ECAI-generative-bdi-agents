package it.unibo.jakta.playground.domesticrobot.environment

import it.unibo.jakta.playground.gridworld.model.Cell
import it.unibo.jakta.playground.gridworld.model.Direction
import it.unibo.jakta.playground.gridworld.model.Grid

data class HouseState(
    val fridgeOpen: Boolean = false,
    val carryingBeer: Boolean = false,
    val sipCount: Int = START_SIP_COUNT,
    val availableBeers: Int = START_BEER_COUNT,
    val gridSize: Int = DEFAULT_GRID_SIZE,
    val robotPosition: Cell = DEFAULT_START_POSITION,
    val grid: Grid = Grid(gridSize),
    val objects: Map<String, Cell> = defaultObjects,
) {
    fun openFridge() = copy(fridgeOpen = true)

    fun closeFridge() = copy(fridgeOpen = false)

    fun getBeer() =
        if (fridgeOpen && availableBeers > 0 && !carryingBeer) {
            copy(
                availableBeers = availableBeers - 1,
                carryingBeer = true,
            )
        } else {
            null
        }

    fun addBeer(numBeers: Int = 1) = copy(availableBeers = availableBeers + numBeers)

    fun moveTowards(objectToReach: String): HouseState? {
        val targetPosition = objects[objectToReach]
        return if (targetPosition == null || robotPosition.isOn(targetPosition)) {
            null
        } else {
            val direction = robotPosition.directionTo(targetPosition)
            direction?.let { dir ->
                val newRobotPosition = moveRobot(robotPosition, dir)
                newRobotPosition?.let { copy(robotPosition = it) }
            }
        }
    }

    private fun moveRobot(
        currentPosition: Cell,
        direction: Direction,
    ): Cell? {
        val newPosition = currentPosition.move(direction)
        return if (grid.isInBoundaries(newPosition) && !grid.isObstacle(newPosition)) {
            Cell(newPosition.x, newPosition.y)
        } else {
            null
        }
    }

    fun handInBeer() =
        if (carryingBeer) {
            copy(
                sipCount = MAX_SIP_COUNT,
                carryingBeer = false,
            )
        } else {
            null
        }

    fun sipBeer() =
        if (sipCount > 0) {
            copy(sipCount = sipCount - 1)
        } else {
            null
        }

    companion object {
        const val MAX_SIP_COUNT = 3
        const val START_SIP_COUNT = 0
        const val START_BEER_COUNT = 1

        internal const val DEFAULT_GRID_SIZE = 5
        internal val defaultObjects =
            mapOf(
                "fridge" to Cell(0, 0),
                "owner" to Cell(DEFAULT_GRID_SIZE - 1, DEFAULT_GRID_SIZE - 1),
            )

        internal val DEFAULT_START_POSITION =
            Cell(DEFAULT_GRID_SIZE / 2, DEFAULT_GRID_SIZE / 2)
    }
}
