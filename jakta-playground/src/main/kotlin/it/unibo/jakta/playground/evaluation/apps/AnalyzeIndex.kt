package it.unibo.jakta.playground.evaluation.apps

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import com.jillesvangurp.ktsearch.parseHit
import com.jillesvangurp.ktsearch.searchAfter
import com.jillesvangurp.searchdsls.querydsl.matchAll
import it.unibo.jakta.agents.bdi.engine.logging.loggers.JaktaLogger.Companion.extractHostnameAndPort
import it.unibo.jakta.agents.bdi.engine.serialization.modules.JaktaJsonComponent
import it.unibo.jakta.playground.ModuleLoader
import it.unibo.jakta.playground.evaluation.KTSearch
import it.unibo.jakta.playground.evaluation.KTSearch.version
import it.unibo.jakta.playground.evaluation.LogEntry
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.runBlocking

/**
 * Command-line tool for analyzing OpenSearch/Elasticsearch indices containing Jakta log entries.
 *
 * This command connects to an OpenSearch/Elasticsearch instance and retrieves all documents from a
 * dynamically determined index based on the provided hierarchy of identifiers (MAS ID,
 * Agent ID, PGP ID). The tool prints out the event type of each log entry found in the index.
 *
 * The target index name follows a hierarchical naming pattern:
 * - Default: `jakta-default` (when no IDs specified)
 * - MAS level: `jakta-mas-{masId}` (when only MAS ID specified)
 * - Agent level: `jakta-mas-{masId}-agent-{agentId}` (when MAS and Agent IDs specified)
 * - PGP level: `jakta-mas-{masId}-agent-{agentId}-pgp-{pgpId}` (when all IDs specified)
 *
 * @property masId Optional MAS (Multi-Agent System) identifier for index selection
 * @property agentId Optional Agent identifier for index selection (requires masId)
 * @property pgpId Optional PGP identifier for index selection (requires masId and agentId)
 * @property url OpenSearch/Elasticsearch server URL (defaults to http://localhost:9200)
 */
class AnalyzeIndex : CliktCommand() {
    private val masId: String? by option()

    private val agentId: String? by option()

    private val pgpId: String? by option()

    private val url: String by option()
        .default("http://localhost:9200")

    init {
        ModuleLoader.loadModules()
    }

    override fun run() {
        runBlocking {
            val (host, port) = extractHostnameAndPort(url)
            val client = KTSearch.createClient(host, port ?: 9200)
            println("Querying at $url (version: ${client.version()})")

            val indexName =
                when {
                    masId == null -> "jakta-default"
                    agentId == null -> "jakta-mas-$masId"
                    pgpId == null -> "jakta-mas-$masId-agent-$agentId"
                    else -> "jakta-mas-$masId-agent-$agentId-pgp-$pgpId"
                }

            println("Using index: $indexName")

            val (resp, hitsFlow) =
                client.searchAfter(indexName) {
                    resultSize = 500
                    query = matchAll()
                }

            println("reported result set size ${resp.hits?.total?.value}")

            hitsFlow
                .mapNotNull { hit ->
                    val doc = hit.parseHit<LogEntry>(JaktaJsonComponent.json)
                    println(doc.message.event)
                }
        }
    }
}

fun main(args: Array<String>) =
    AnalyzeIndex()
        .context { terminal = Terminal(ansiLevel = AnsiLevel.TRUECOLOR, interactive = true) }
        .main(args)
