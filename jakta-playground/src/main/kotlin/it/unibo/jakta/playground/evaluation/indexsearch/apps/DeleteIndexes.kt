package it.unibo.jakta.playground.evaluation.indexsearch.apps

import com.jillesvangurp.ktsearch.deleteIndex
import it.unibo.jakta.playground.evaluation.indexsearch.IndexSearch.getAgentsFromMas
import it.unibo.jakta.playground.evaluation.indexsearch.IndexSearch.getMasTraces
import it.unibo.jakta.playground.evaluation.indexsearch.IndexSearch.getPlanGenProceduresFromAgent
import it.unibo.jakta.playground.evaluation.indexsearch.KTSearch
import it.unibo.jakta.playground.evaluation.indexsearch.KTSearch.retrieveIndexNames
import it.unibo.jakta.playground.evaluation.indexsearch.KTSearch.version

/**
 * Deletes all search indexes created by running a Multi-Agent System (MAS).
 *
 * This cleanup function connects to a KTSearch client and systematically removes
 * all indexes associated with MAS experimental runs. It traverses the hierarchical
 * structure of stored traces and deletes:
 * - The default "jakta-default" index
 * - All MAS (Multi-Agent System) trace indexes
 * - All Agent trace indexes within each MAS
 * - All PGP (Plan Generation Procedure) trace indexes within each agent
 *
 * **Warning**: This operation is destructive and permanently removes all MAS-related
 * experimental data from the search indexes. Use with caution.
 */
suspend fun main() {
    val client = KTSearch.createClient()
    println("Querying ${client.version()}")

    val indexNames = client.retrieveIndexNames()
    val masOnlyIndices = getMasTraces(indexNames)

    client.deleteIndex("jakta-default")

    masOnlyIndices
        .forEach { masIdx ->
            val masId = masIdx.substringAfter("-mas-")
            println("Deleting mas trace with id: $masId")
            val agentIndices =
                getAgentsFromMas(
                    indexNames,
                    masId,
                )
            client.deleteIndex(masIdx)
            agentIndices.forEach { agentIdx ->
                val agentId = agentIdx.substringAfter("-agent-")
                println("Deleting agent trace with id: $agentId")
                val planGenProcedures =
                    getPlanGenProceduresFromAgent(
                        indexNames,
                        masId,
                        agentId,
                    )
                client.deleteIndex(agentIdx)
                planGenProcedures.forEach { pgpIdx ->
                    val pgpId = pgpIdx.substringAfter("-pgp-")
                    println("Deleting PGP trace with id: $pgpId")
                    client.deleteIndex(pgpIdx)
                }
            }
        }
}
