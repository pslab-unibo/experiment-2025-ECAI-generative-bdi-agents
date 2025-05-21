package it.unibo.jakta.playground.evaluation

object IndexSearch {
    private const val UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"
    private val masOnlyPattern = "^jakta-mas-$UUID_PATTERN$".toRegex()
    private val agentOnlyPattern = "^jakta-mas-$UUID_PATTERN-agent-$UUID_PATTERN$".toRegex()
    private val pgpPattern = "^jakta-mas-$UUID_PATTERN-agent-$UUID_PATTERN-pgp-$UUID_PATTERN$".toRegex()

    fun getMasTraces(indexNames: List<String>) = indexNames.filter { masOnlyPattern.matches(it) }

    fun getAgentsFromMas(
        indexNames: List<String>,
        masId: String,
    ) = indexNames.filter { it.startsWith("jakta-mas-$masId-agent-") && agentOnlyPattern.matches(it) }

    fun getPlanGenProceduresFromAgent(
        indexNames: List<String>,
        masId: String,
        agentId: String,
    ) = indexNames.filter { it.startsWith("jakta-mas-$masId-agent-$agentId-pgp-") && pgpPattern.matches(it) }
}
