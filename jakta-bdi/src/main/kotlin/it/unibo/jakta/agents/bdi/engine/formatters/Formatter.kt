package it.unibo.jakta.agents.bdi.engine.formatters

/**
 * Generic formatter interface for converting objects of type T to strings.
 */
fun interface Formatter<T> {
    /**
     * Formats a single item of type T into a string representation.
     *
     * @param item The item to format
     * @return The formatted string representation, or null if the item cannot be formatted
     */
    fun format(item: T): String?
}
