package it.unibo.jakta.playground.evaluation.apps

import it.unibo.jakta.playground.evaluation.IndexSearch.getAgentsFromMas
import it.unibo.jakta.playground.evaluation.IndexSearch.getMasTraces
import it.unibo.jakta.playground.evaluation.IndexSearch.getPlanGenProceduresFromAgent
import it.unibo.jakta.playground.evaluation.KTSearch
import it.unibo.jakta.playground.evaluation.KTSearch.retrieveIndexNames
import it.unibo.jakta.playground.evaluation.KTSearch.version

/**
 * Lists and displays the hierarchical structure of search indexes containing MAS traces.
 *
 * This function connects to a KTSearch client and discovers the organizational structure
 * of stored traces by traversing through:
 * - MAS (Multi-Agent System) traces at the top level
 * - Agent traces within each MAS
 * - PGP (Plan Generation Procedure) traces within each agent
 *
 * The output provides a complete inventory of available traces, showing the nested
 * relationship between MAS instances, their constituent agents, and the plan generation
 * procedures executed by each agent.
 *
 * Index naming convention expected:
 * - MAS indices: "*-mas-{masId}"
 * - Agent indices: "*-agent-{agentId}"
 * - PGP indices: "*-pgp-{pgpId}"
 *
 * @see AnalyzePGP for tools to analyze the discovered PGP traces
 */
fun main() {
    val client = KTSearch.createClient()
    println("Querying ${client.version()}")

    val indexNames = client.retrieveIndexNames()
    val masOnlyIndices = getMasTraces(indexNames)
    masOnlyIndices
        .forEach { masId ->
            val masId = masId.substringAfter("-mas-")
            println("Found mas trace with id: $masId")
            val agentIndices =
                getAgentsFromMas(
                    indexNames,
                    masId,
                )
            agentIndices.forEach { agentId ->
                val agentId = agentId.substringAfter("-agent-")
                println("Found agent trace with id: $agentId")
                val planGenProcedures =
                    getPlanGenProceduresFromAgent(
                        indexNames,
                        masId,
                        agentId,
                    )
                planGenProcedures.forEach { pgpId ->
                    val pgpId = pgpId.substringAfter("-pgp-")
                    println("Found PGP trace with id: $pgpId")
                }
            }
        }
}
