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

    @Test
    fun checkForFirstVar() {
        try {
            assertEquals(Dashboard.getDouble("Constants.First.PROP"), 2.0)
        } catch (de: DashboardException) {
            fail<Any>("Constants.First.PROP not found")
        }
    }

    @Test
    fun checkForSecondVar() {
        try {
            assertEquals(Dashboard.getDouble("Constants.Second.PROP"), 3.0)
        } catch (de: DashboardException) {
            fail<Any>("Constants.Second.PROP not found")
        }
    }

    @Test
    fun checkForOuterNestVar() {
        try {
            assertEquals(Dashboard.getDouble("Constants.Nest.PROP"), 4.0)
        } catch (de: DashboardException) {
            fail<Any>("Constants.Nest.PROP not found")
        }
    }

    @Test
    fun checkForInnerNestVar() {
        try {
            assertEquals(Dashboard.getDouble("Constants.Nest.Inner.PROP"), 5.0)
        } catch (de: DashboardException) {
            fail<Any>("Constants.Nest.Inner.PROP not found")
        }
    }
}

object Constants {

    public fun initConstants() {
        println("init props")
    }

    public var PROP by DashboardVar(1.0)

    object First {
        fun initProps() {
            println("init First")
        }
        public var PROP by DashboardVar(2.0)
    }

    object Second {
        fun initProps() {
            println("init Second")
        }
        public var PROP by DashboardVar(3.0)
    }

    object Nest {
        fun initProps() {
            println("init Nested")
            Inner.initProps()
        }
        public var PROP by DashboardVar(4.0)
        object Inner {
            fun initProps() {
                println("init Nest.Inner")
            }
            public var PROP by DashboardVar(5.0)
        }
    }
}
