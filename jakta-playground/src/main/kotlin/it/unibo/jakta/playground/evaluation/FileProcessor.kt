package it.unibo.jakta.playground.evaluation

import it.unibo.jakta.agents.bdi.engine.logging.loggers.JaktaLogger
import it.unibo.jakta.agents.bdi.engine.serialization.modules.JaktaJsonComponent
import kotlinx.io.IOException
import java.io.BufferedReader
import java.io.File

object FileProcessor {
    fun writeToFile(
        content: String,
        file: File,
        description: String,
    ) = try {
        file.writeText(content)
        println("$description written to ${file.name}")
    } catch (e: IOException) {
        println("Error writing to file: ${e.message}")
    }

    private fun processLines(
        reader: BufferedReader,
        logger: JaktaLogger? = null,
        processFunction: (LogEntry) -> Boolean,
    ): Boolean {
        var lineCount = 0
        var errorCount = 0
        var shouldContinue = true

        reader.useLines { lines ->
            for (line in lines) {
                if (!shouldContinue) break

                lineCount++
                val logEntry =
                    try {
                        JaktaJsonComponent.json.decodeFromString<LogEntry>(line)
                    } catch (e: Exception) {
                        errorCount++
                        logger?.warn { "Could not parse line $lineCount: ${e.message} as a LogEntry." }
                        null
                    }
                logEntry?.let {
                    shouldContinue = processFunction(it)
                }
            }
        }
        logger?.info { "Processing complete. Total entries: $lineCount, Events not parseable: $errorCount" }
        return shouldContinue
    }

    fun processFile(
        file: File,
        logger: JaktaLogger? = null,
        processFunction: (LogEntry) -> Boolean,
    ) {
        val reader = file.bufferedReader()
        processLines(reader, logger, processFunction)
    }
}
