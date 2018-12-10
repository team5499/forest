package tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Tag

import org.team5499.dashboard.Dashboard

@Tag("integration")
class ApplicationTest {

    @Test
    fun runServer() {
        println("starting server...")
        Dashboard.start()
    }
}
