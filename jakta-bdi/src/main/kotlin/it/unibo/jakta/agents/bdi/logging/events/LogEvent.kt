package it.unibo.jakta.agents.bdi.logging.events

interface LogEvent {
    val name: String get() = this.javaClass.simpleName
    val description: String
    val metadata: Map<String, Any?> get() = buildMap {
        put("type", name)
    }
}
