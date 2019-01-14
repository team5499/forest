package tests

import org.junit.jupiter.api.Test

import org.team5499.dashboard.Utils

class UtilsTest {

    @Test
    fun testRelativize() {
        val contents = Utils.getResourceDirectoryContents(this, "testContents")
        val relativeContents = Utils.convertToRelativePaths(this, contents, "testContents")
        println("Absolute Paths:")
        contents.forEach({
            path: String ->
            println("Absolute: " + path)
        })
        println("Relative Paths:")
        relativeContents.forEach({
            newpath: String ->
            println("Relative: " + newpath)
        })
    }
}
