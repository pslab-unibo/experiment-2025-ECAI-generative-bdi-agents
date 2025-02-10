package it.unibo.jakta.agents.bdi.actions

import it.unibo.tuprolog.solve.Signature

enum class Type {
    Atom,
    Var,
    Struct,
    String,
    Integer,
    Real,
}

enum class Mode {
    Input,
    Output,
    InOut,
}

data class Parameter(
    val name: String,
    val type: Type,
    val mode: Mode,
)

data class SignatureWithDoc(
    val signature: Signature,
    val description: String = "",
    val parameters: List<Parameter> = emptyList(),
) {
    constructor(name: String, arity: Int) : this(Signature(name, arity))
    constructor(name: String, description: String, parameters: List<Parameter>) :
        this(Signature(name, parameters.size), description, parameters)

    val name: String = signature.name
    val arity: Int = signature.arity

    fun toPrologDoc(): String {
        val paramsDoc = parameters.joinToString(", ") { param: Parameter ->
            "${when (param.mode) {
                Mode.Input -> "+"
                Mode.Output -> "-"
                Mode.InOut -> "?"
            }}${param.name}:${param.type.toString().lowercase()}"
        }

        val params = if (parameters.isEmpty()) "" else "($paramsDoc)"

        return """
        |%! $description
        |$name$params.
        """.trimMargin("|")
    }
}
