package it.unibo.jakta.playground.evaluation

data class SemanticAlignmentResult(
    val notParsed: Int,
    val alreadyAdmissible: Set<Any>,
    val admissibleNotUsed: Set<Any>,
    val usedNotAdmissible: Set<Any>,
)
