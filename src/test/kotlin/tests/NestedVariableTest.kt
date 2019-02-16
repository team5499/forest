package tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail

import org.team5499.dashboard.Dashboard
import org.team5499.dashboard.DashboardVar
import org.team5499.dashboard.DashboardException

class NestedVariableTest {
    companion object {
        @BeforeAll
        @JvmStatic
        fun initDashboard() {
            Dashboard.start(this, "playgroundConf.json")
            Constants.initConstants()
        }

        @AfterAll
        @JvmStatic
        fun tearDownDashboard() {
            Dashboard.stop()
        }
    }

    @Test
    fun checkForOuterVar() {
        try {
            assertEquals(Dashboard.getDouble("Constants.PROP"), 1.0)
        } catch (de: DashboardException) {
            fail<Any>("Constants.PROP not found")
        }
    }
}

object Constants {

    public fun initConstants() {
        DashboardVar.initClassProps(Constants::class)
    }

    public var PROP by DashboardVar(1.0)

    object First {
        public var PROP by DashboardVar(2.0)
    }

    object Second {
        public var PROP by DashboardVar(3.0)
    }

    object Nest {
        public var PROP by DashboardVar(4.0)
        object Inner {
            public var PROP by DashboardVar(5.0)
        }
    }
}
