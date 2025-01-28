package it.unibo.jakta.llm.output

import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.arguments.ManualLanguageFeatureSetting
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.Services
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream

object Compiler {
    fun compile(
        input: File,
        output: File,
    ): Pair<Boolean, String> {
        val stringWriter = ByteArrayOutputStream()
        val printStream = PrintStream(stringWriter)

        val result =
            K2JVMCompiler()
                .run {
                    val args =
                        K2JVMCompilerArguments().apply {
                            freeArgs = listOf(input.absolutePath)
                            destination = output.absolutePath
                            classpath = System.getProperty("java.class.path")
                            noStdlib = true
                            noReflect = true
                            reportPerf = false
                            internalArguments =
                                listOf(
                                    ManualLanguageFeatureSetting(
                                        LanguageFeature.ContextReceivers,
                                        LanguageFeature.State.ENABLED,
                                        "-Xcontext-receivers",
                                    ),
                                )
                        }
                    exec(
                        PrintingMessageCollector(
                            printStream,
                            MessageRenderer.WITHOUT_PATHS,
                            false,
                        ),
                        Services.Companion.EMPTY,
                        args,
                    )
                }.code == 0

        printStream.close()

        return result to stringWriter.toString()
    }
}
