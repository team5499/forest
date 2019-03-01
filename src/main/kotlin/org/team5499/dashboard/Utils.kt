package org.team5499.dashboard

import java.io.StringWriter
import java.io.InputStream
import org.apache.commons.io.IOUtils

object Utils {
    val os = System.getProperty("os.name").toLowerCase()

    fun readResourceAsString(obj: Any, path: String, encoding: String = "UTF-8"): String {
        var writer: StringWriter = StringWriter()
        var inputStream: InputStream = obj::class.java.getClassLoader().getResourceAsStream(path)
        IOUtils.copy(inputStream, writer, encoding)
        return writer.toString()
    }

    fun isWindows(): Boolean {

        return (os.indexOf("win") >= 0)
    }

    fun isMac(): Boolean {

        return (os.indexOf("mac") >= 0)
    }

    fun isUnix(): Boolean {

        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0)
    }

    fun isSolaris(): Boolean {

        return (os.indexOf("sunos") >= 0)
    }
}
