package it.unibo.jakta.playground

import it.unibo.jakta.agents.bdi.dsl.MasScope
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.logging.LoggingConfig

fun main() = mas {
    guardLit()
    loggingConfig = LoggingConfig(logToConsole = true, logToFile = false)
}.start()

fun MasScope.guardLit() =
    agent("guardLit") {
        goals {
            achieve("""count from 0 to 10 using `count(0, 10)`""")
        }
        plans {
            +achieve("`count(@n, @n)` :- count down to `@n`") then (
                """
                Write the last number using `achieve(print_number(@n))`.
                Stop counting with `execute(stop)`.
                """
                )

            +achieve("`count(@n, @m)` count from `@n` to `@m`") onlyIf (
                """
                To keep counting, `@n` must be lower than `@m`, so `<(@n, @m)`.
                Then you should compute the next number `@s` from `@n`, using `is(@s, @n + 1)`.
                """
                ) then (
                """
                Write the current number using `achieve(print_number(@n))`.
                Keep counting with `achieve(count(@s, @m))`.
                """
                )

            +achieve("`print_number(@n)` print the number `@n` if it is odd") onlyIf (
                """
                If `is(1, @n rem 2)` holds then you can print.
                """
                ) then (
                """
                Write that the number is odd using `execute(print("number is odd", @n))`.
                """
                )

            +achieve("`print_number(@n)` print the number `@n` if it is even") onlyIf (
                """
                If `is(0, @n rem 2)` holds then you can print.
                """
                ) then (
                """
                Write that the number is even using `execute(print("number is even", @n))`.
                """
                )
        }
    }
