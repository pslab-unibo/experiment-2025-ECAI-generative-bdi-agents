package it.unibo.jakta.playground.evaluation

import it.unibo.jakta.agents.bdi.engine.logging.loggers.JaktaLogger.Companion.EXTENSION
import it.unibo.jakta.agents.bdi.engine.logging.loggers.JaktaLogger.Companion.UUID_PATTERN
import it.unibo.jakta.agents.bdi.engine.logging.loggers.JaktaLogger.Companion.extractLastComponent
import it.unibo.jakta.agents.bdi.engine.logging.loggers.JaktaLogger.Companion.extractLastId
import it.unibo.jakta.agents.bdi.engine.logging.loggers.JaktaLogger.Companion.logFileRegex
import java.io.File

object LogFileUtils {
    fun findMasLogFile(expDir: String): File? =
        File(expDir)
            .listFiles { file ->
                file.name.matches(logFileRegex) &&
                    extractLastComponent(file.name) == "Mas"
            }?.let { files ->
                files.takeIf { it.size == 1 }?.first()?.also {
                    if (files.size > 1) println("Warning: Multiple Mas-*.$EXTENSION files found. Using first.")
                }
            } ?: run {
            println("No Mas-*.$EXTENSION file found")
            null
        }

    fun extractAgentLogFiles(
        expDir: String,
        masLogFile: File,
    ): List<File> {
        val masId = extractLastId(masLogFile.name) ?: return emptyList()
        return File(expDir)
            .listFiles { file ->
                file.name.matches(logFileRegex) &&
                    countUuids(file.name) == 2 &&
                    file.name.startsWith("Mas-$masId-")
            }?.toList() ?: emptyList()
    }

    fun extractPgpLogFiles(
        expDir: String,
        agentLogFile: File,
    ): List<File> =
        File(expDir)
            .listFiles { file ->
                file.name.matches(logFileRegex) &&
                    countUuids(file.name) == 3 &&
                    file.name.startsWith(agentLogFile.name.substringBeforeLast("-"))
            }?.toList() ?: emptyList()

    fun countUuids(filename: String): Int {
        val pattern = Regex(UUID_PATTERN)
        return pattern.findAll(filename).count()
    }
}
