package it.unibo.jakta.playground.explorer.gridworld

data class GridWorldState(
    val grid: Grid,
    val agentPosition: Position,
    val objects: Map<String, Position>,
    val availableDirections: Set<Direction>,
)
