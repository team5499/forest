package org.team5499.dashboard

import java.io.StringWriter
import java.io.InputStream
import java.io.File
import java.nio.file.Paths
import java.nio.file.Path
import java.net.URL
import org.apache.commons.io.IOUtils

object Utils {
    fun readResourceAsString(obj: Any, path: String, encoding: String = "UTF-8"): String {
        var writer: StringWriter = StringWriter()
        var inputStream: InputStream = obj::class.java.getClassLoader().getResourceAsStream(path)
        IOUtils.copy(inputStream, writer, encoding)
        return writer.toString()
    }

    fun getResourceDirectoryContents(obj: Any, path: String): Array<String> {
        val url: URL = obj::class.java.getClassLoader().getResource(path)
        val path: String = url.path
        val paths = mutableListOf<String>()
        File(path).listFiles().forEach({
            file: File ->
            paths.add(file.path)
        })
        return paths.toTypedArray()
    }

    fun convertToRelativePaths(obj: Any, paths: Array<String>, parent: String): Array<String> {
        val parentPath: Path = Paths.get(obj::class.java.getClassLoader().getResource(parent).path)
        println("parent path: $parentPath")
        var newPaths = mutableListOf<String>()
        paths.forEach({
            path: String ->
            newPaths.add(parentPath.relativize(Paths.get(path)).normalize().toString())
        })
        return newPaths.toTypedArray()
    }
}
