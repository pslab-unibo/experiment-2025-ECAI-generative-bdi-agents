package it.unibo.jakta.llm.output

import java.io.File
import java.net.URLClassLoader

class CodeHandler(
    byteCodePath: String,
) {
    private fun saveCode(
        code: String,
        sourcePath: String,
    ): File {
        val f = File(sourcePath)
        f.createNewFile()
        f.writeText(code)
        return f
    }

    fun compileResponse(
        code: String,
        sourcePath: String,
        bytecodeBuildDirectory: String,
    ): String? {
        val inputFile = saveCode(code, sourcePath)
        val outputFile = File(bytecodeBuildDirectory)
        val (res, errorMsg) = Compiler.compile(inputFile, outputFile)
        return if (res == true) {
            null
        } else {
            errorMsg
        }
    }

    private val loader =
        URLClassLoader(
            arrayOf(File(byteCodePath).toURI().toURL()),
            this::class.java.classLoader,
        )

    @Suppress("unchecked_cast")
    fun <T : Any> loadObject(className: String): T? = loader.loadClass(className).kotlin.objectInstance as? T
}
