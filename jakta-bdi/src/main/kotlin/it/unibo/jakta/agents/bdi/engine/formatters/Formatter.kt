package it.unibo.jakta.agents.bdi.engine.formatters

/**
 * Generic formatter interface for converting objects of type T to strings.
 */
interface Formatter<T> {
    /**
     * Formats a single item of type T into a string representation.
     *
     * @param item The item to format
     * @return The formatted string representation, or null if the item cannot be formatted
     */
    fun format(item: T): String

    /**
     * Formats a collection of items with type T into a list of strings.
     *
     * @param items The items to format
     * @return A list of formatted string representations
     */
    fun format(items: Iterable<T>): List<String> = items.mapNotNull { format(it) }
}
