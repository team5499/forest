package tests

import org.junit.jupiter.api.Test

import org.team5499.dashboard.Dashboard
import org.team5499.dashboard.DashboardVar

class StartupTimeTest {

    object Constants {
        fun initConstants() {
            DashboardVar.initClassProps(this::class)
            println(DashboardVar.count)
        }
        public var CONSTANT_1 by DashboardVar(1.0)
        public var CONSTANT_2 by DashboardVar(1.0)
        public var CONSTANT_3 by DashboardVar(1.0)
        public var CONSTANT_4 by DashboardVar(1.0)
        public var CONSTANT_5 by DashboardVar(1.0)
        public var CONSTANT_6 by DashboardVar(1.0)
        public var CONSTANT_7 by DashboardVar(1.0)
        public var CONSTANT_8 by DashboardVar(1.0)
        public var CONSTANT_9 by DashboardVar(1.0)
        public var CONSTANT_10 by DashboardVar(1.0)
        public var CONSTANT_11 by DashboardVar(1.0)
        public var CONSTANT_12 by DashboardVar(1.0)
        public var CONSTANT_13 by DashboardVar(1.0)
        public var CONSTANT_14 by DashboardVar(1.0)
        public var CONSTANT_15 by DashboardVar(1.0)
        public var CONSTANT_16 by DashboardVar(1.0)
        public var CONSTANT_17 by DashboardVar(1.0)
        public var CONSTANT_18 by DashboardVar(1.0)
        public var CONSTANT_19 by DashboardVar(1.0)
        public var CONSTANT_20 by DashboardVar(1.0)
        public var CONSTANT_21 by DashboardVar(1.0)
        public var CONSTANT_22 by DashboardVar(1.0)
        public var CONSTANT_23 by DashboardVar(1.0)
        public var CONSTANT_24 by DashboardVar(1.0)
        public var CONSTANT_25 by DashboardVar(1.0)
        object Nested1 {
            public var CONSTANT_1 by DashboardVar(1.0)
            public var CONSTANT_2 by DashboardVar(1.0)
            public var CONSTANT_3 by DashboardVar(1.0)
            public var CONSTANT_4 by DashboardVar(1.0)
            public var CONSTANT_5 by DashboardVar(1.0)
            public var CONSTANT_6 by DashboardVar(1.0)
            public var CONSTANT_7 by DashboardVar(1.0)
            public var CONSTANT_8 by DashboardVar(1.0)
            public var CONSTANT_9 by DashboardVar(1.0)
            public var CONSTANT_10 by DashboardVar(1.0)
            public var CONSTANT_11 by DashboardVar(1.0)
            public var CONSTANT_12 by DashboardVar(1.0)
            public var CONSTANT_13 by DashboardVar(1.0)
            public var CONSTANT_14 by DashboardVar(1.0)
            public var CONSTANT_15 by DashboardVar(1.0)
            public var CONSTANT_16 by DashboardVar(1.0)
            public var CONSTANT_17 by DashboardVar(1.0)
            public var CONSTANT_18 by DashboardVar(1.0)
            public var CONSTANT_19 by DashboardVar(1.0)
            public var CONSTANT_20 by DashboardVar(1.0)
            public var CONSTANT_21 by DashboardVar(1.0)
            public var CONSTANT_22 by DashboardVar(1.0)
            public var CONSTANT_23 by DashboardVar(1.0)
            public var CONSTANT_24 by DashboardVar(1.0)
            public var CONSTANT_25 by DashboardVar(1.0)
        }
        object Nested2 {
            public var CONSTANT_1 by DashboardVar(1.0)
            public var CONSTANT_2 by DashboardVar(1.0)
            public var CONSTANT_3 by DashboardVar(1.0)
            public var CONSTANT_4 by DashboardVar(1.0)
            public var CONSTANT_5 by DashboardVar(1.0)
            public var CONSTANT_6 by DashboardVar(1.0)
            public var CONSTANT_7 by DashboardVar(1.0)
            public var CONSTANT_8 by DashboardVar(1.0)
            public var CONSTANT_9 by DashboardVar(1.0)
            public var CONSTANT_10 by DashboardVar(1.0)
            public var CONSTANT_11 by DashboardVar(1.0)
            public var CONSTANT_12 by DashboardVar(1.0)
            public var CONSTANT_13 by DashboardVar(1.0)
            public var CONSTANT_14 by DashboardVar(1.0)
            public var CONSTANT_15 by DashboardVar(1.0)
            public var CONSTANT_16 by DashboardVar(1.0)
            public var CONSTANT_17 by DashboardVar(1.0)
            public var CONSTANT_18 by DashboardVar(1.0)
            public var CONSTANT_19 by DashboardVar(1.0)
            public var CONSTANT_20 by DashboardVar(1.0)
            public var CONSTANT_21 by DashboardVar(1.0)
            public var CONSTANT_22 by DashboardVar(1.0)
            public var CONSTANT_23 by DashboardVar(1.0)
            public var CONSTANT_24 by DashboardVar(1.0)
            public var CONSTANT_25 by DashboardVar(1.0)
        }
        object Nested4 {
            public var CONSTANT_1 by DashboardVar(1.0)
            public var CONSTANT_2 by DashboardVar(1.0)
            public var CONSTANT_3 by DashboardVar(1.0)
            public var CONSTANT_4 by DashboardVar(1.0)
            public var CONSTANT_5 by DashboardVar(1.0)
            public var CONSTANT_6 by DashboardVar(1.0)
            public var CONSTANT_7 by DashboardVar(1.0)
            public var CONSTANT_8 by DashboardVar(1.0)
            public var CONSTANT_9 by DashboardVar(1.0)
            public var CONSTANT_10 by DashboardVar(1.0)
            public var CONSTANT_11 by DashboardVar(1.0)
            public var CONSTANT_12 by DashboardVar(1.0)
            public var CONSTANT_13 by DashboardVar(1.0)
            public var CONSTANT_14 by DashboardVar(1.0)
            public var CONSTANT_15 by DashboardVar(1.0)
            public var CONSTANT_16 by DashboardVar(1.0)
            public var CONSTANT_17 by DashboardVar(1.0)
            public var CONSTANT_18 by DashboardVar(1.0)
            public var CONSTANT_19 by DashboardVar(1.0)
            public var CONSTANT_20 by DashboardVar(1.0)
            public var CONSTANT_21 by DashboardVar(1.0)
            public var CONSTANT_22 by DashboardVar(1.0)
            public var CONSTANT_23 by DashboardVar(1.0)
            public var CONSTANT_24 by DashboardVar(1.0)
            public var CONSTANT_25 by DashboardVar(1.0)
        }
    }

    @Test
    fun classInitTest() {
        val startTime = System.nanoTime()
        Constants.initConstants()
        val stopTime = System.nanoTime()
        println(stopTime - startTime)
    }

    @Test
    fun startupTime() {
        val startTime = System.nanoTime()
        Dashboard.start(this, "playgroundConf.json")
        Constants.initConstants()
        val stopTime = System.nanoTime()
        println(stopTime - startTime)
    }
}
