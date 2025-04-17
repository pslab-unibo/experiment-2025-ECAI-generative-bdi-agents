package it.unibo.jakta.generationstrategies.lm.pipeline.parsing.tagprocessor

import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ParsedStatement.SimpleStatement

class SimpleProcessor : TagProcessor {
    private val items = mutableListOf<SimpleStatement>()

    override fun process(content: String): Boolean {
        val initialSize = items.size

        content.lines()
            .filter { it.trim().startsWith('`') }
            .forEach {
                val rawLine = it
                val processedLine = rawLine.trim().removeSurrounding("`")
                items.add(SimpleStatement(rawLine, processedLine))
            }

        return items.size > initialSize // at least an item is added
    }

    override fun getItems(): List<SimpleStatement> = items.toList()
}
