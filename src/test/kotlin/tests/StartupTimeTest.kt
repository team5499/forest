package tests

import org.junit.jupiter.api.Test

import org.team5499.dashboard.DashboardVar

class StartupTimeTest {

    object ConstantsNormal {
        fun initConstants() {
            Nested1.initConstants()
            Nested2.initConstants()
            Nested3.initConstants()
        }
        public var CONSTANT_1 = 1.0
        public var CONSTANT_2 = 1.0
        public var CONSTANT_3 = 1.0
        public var CONSTANT_4 = 1.0
        public var CONSTANT_5 = 1.0
        public var CONSTANT_6 = 1.0
        public var CONSTANT_7 = 1.0
        public var CONSTANT_8 = 1.0
        public var CONSTANT_9 = 1.0
        public var CONSTANT_10 = 1.0
        public var CONSTANT_11 = 1.0
        public var CONSTANT_12 = 1.0
        public var CONSTANT_13 = 1.0
        public var CONSTANT_14 = 1.0
        public var CONSTANT_15 = 1.0
        public var CONSTANT_16 = 1.0
        public var CONSTANT_17 = 1.0
        public var CONSTANT_18 = 1.0
        public var CONSTANT_19 = 1.0
        public var CONSTANT_20 = 1.0
        public var CONSTANT_21 = 1.0
        public var CONSTANT_22 = 1.0
        public var CONSTANT_23 = 1.0
        public var CONSTANT_24 = 1.0
        public var CONSTANT_25 = 1.0
        object Nested1 {
            fun initConstants() {
                println("init Nested1")
            }
            public var CONSTANT_1 = 1.0
            public var CONSTANT_2 = 1.0
            public var CONSTANT_3 = 1.0
            public var CONSTANT_4 = 1.0
            public var CONSTANT_5 = 1.0
            public var CONSTANT_6 = 1.0
            public var CONSTANT_7 = 1.0
            public var CONSTANT_8 = 1.0
            public var CONSTANT_9 = 1.0
            public var CONSTANT_10 = 1.0
            public var CONSTANT_11 = 1.0
            public var CONSTANT_12 = 1.0
            public var CONSTANT_13 = 1.0
            public var CONSTANT_14 = 1.0
            public var CONSTANT_15 = 1.0
            public var CONSTANT_16 = 1.0
            public var CONSTANT_17 = 1.0
            public var CONSTANT_18 = 1.0
            public var CONSTANT_19 = 1.0
            public var CONSTANT_20 = 1.0
            public var CONSTANT_21 = 1.0
            public var CONSTANT_22 = 1.0
            public var CONSTANT_23 = 1.0
            public var CONSTANT_24 = 1.0
            public var CONSTANT_25 = 1.0
        }
        object Nested2 {
            fun initConstants() {
                println("init Nested2")
            }
            public var CONSTANT_1 = 1.0
            public var CONSTANT_2 = 1.0
            public var CONSTANT_3 = 1.0
            public var CONSTANT_4 = 1.0
            public var CONSTANT_5 = 1.0
            public var CONSTANT_6 = 1.0
            public var CONSTANT_7 = 1.0
            public var CONSTANT_8 = 1.0
            public var CONSTANT_9 = 1.0
            public var CONSTANT_10 = 1.0
            public var CONSTANT_11 = 1.0
            public var CONSTANT_12 = 1.0
            public var CONSTANT_13 = 1.0
            public var CONSTANT_14 = 1.0
            public var CONSTANT_15 = 1.0
            public var CONSTANT_16 = 1.0
            public var CONSTANT_17 = 1.0
            public var CONSTANT_18 = 1.0
            public var CONSTANT_19 = 1.0
            public var CONSTANT_20 = 1.0
            public var CONSTANT_21 = 1.0
            public var CONSTANT_22 = 1.0
            public var CONSTANT_23 = 1.0
            public var CONSTANT_24 = 1.0
            public var CONSTANT_25 = 1.0
        }
        object Nested3 {
            fun initConstants() {
                println("init Nested3")
            }
            public var CONSTANT_1 = 1.0
            public var CONSTANT_2 = 1.0
            public var CONSTANT_3 = 1.0
            public var CONSTANT_4 = 1.0
            public var CONSTANT_5 = 1.0
            public var CONSTANT_6 = 1.0
            public var CONSTANT_7 = 1.0
            public var CONSTANT_8 = 1.0
            public var CONSTANT_9 = 1.0
            public var CONSTANT_10 = 1.0
            public var CONSTANT_11 = 1.0
            public var CONSTANT_12 = 1.0
            public var CONSTANT_13 = 1.0
            public var CONSTANT_14 = 1.0
            public var CONSTANT_15 = 1.0
            public var CONSTANT_16 = 1.0
            public var CONSTANT_17 = 1.0
            public var CONSTANT_18 = 1.0
            public var CONSTANT_19 = 1.0
            public var CONSTANT_20 = 1.0
            public var CONSTANT_21 = 1.0
            public var CONSTANT_22 = 1.0
            public var CONSTANT_23 = 1.0
            public var CONSTANT_24 = 1.0
            public var CONSTANT_25 = 1.0
        }
    }

    object ConstantsDashboard {
        fun initConstants() {
            Nested1.initConstants()
            Nested2.initConstants()
            Nested3.initConstants()
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
            fun initConstants() {
                println("init Nested1")
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
        }
        object Nested2 {
            fun initConstants() {
                println("init Nested2")
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
        }
        object Nested3 {
            fun initConstants() {
                println("init Nested3")
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
        }
    }

    @Test
    fun classInitTest() {
        val dashStartTime = System.nanoTime()
        ConstantsDashboard.initConstants()
        val dashStopTime = System.nanoTime()
        println("Dashboard delegates: ${dashStopTime - dashStartTime}")
        val normalStartTime = System.nanoTime()
        ConstantsDashboard.initConstants()
        val normalStopTime = System.nanoTime()
        println("Normal properties: ${normalStopTime - normalStartTime}")
    }
}
