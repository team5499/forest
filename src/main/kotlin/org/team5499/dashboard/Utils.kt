package org.team5499.dashboard

import java.io.StringWriter
import java.io.InputStream
import org.apache.commons.io.IOUtils

object Utils {
    fun readResourceAsString(obj: Any, path: String, encoding: String = "UTF-8"): String {
        var writer: StringWriter = StringWriter()
        var inputStream: InputStream = obj::class.java.getClassLoader().getResourceAsStream(path)
        IOUtils.copy(inputStream, writer, encoding)
        return writer.toString()
    }
}
