package org.team5499.dashboard

import kotlin.reflect.KProperty
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.KVisibility
import kotlin.reflect.KMutableProperty

class DashboardVar<T>(var initValue: T) {

    companion object {
        var count = 0L

        @SuppressWarnings("EmptyCatchBlock", "TooGenericExceptionCaught")
        fun initClassProps(clazz: KClass<*>) {
            var totalTime = 0L
            val loopStartTime = System.nanoTime()
            val props = clazz.memberProperties
            val afterLoad = System.nanoTime()
            props.forEach({
                val startTime = System.nanoTime()
                if (it.visibility == KVisibility.PUBLIC && it is KMutableProperty<*>) {
                    try {
                        it.getter.call(clazz.objectInstance)
                    } catch (e: Exception) {
                        println("exception")
                    }
                }
                val endTime = System.nanoTime()
                count += (endTime - startTime)
                totalTime += (endTime - startTime)
                // println("${endTime - startTime}: $it")
            })
            val loopEndTime = System.nanoTime()
            println("total: $totalTime - loop: ${loopEndTime - afterLoad} - load: ${afterLoad - loopStartTime}")
            clazz.nestedClasses.forEach({
                initClassProps(it)
            })
        }
    }

    var isInit = false

    fun getDashName(thisRef: Any?, property: KProperty<*>): String {
        val packageLength = thisRef!!::class.java.getPackage().name.length
        return "${thisRef::class.qualifiedName!!.substring(packageLength + 1)}.${property.name}"
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!isInit) {
            Dashboard.setVariable(getDashName(thisRef, property), initValue as Any)
            isInit = true
            // println(getDashName(thisRef, property))
            return initValue
        }
        return Dashboard.getVariable(getDashName(thisRef, property))
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        isInit = true
        Dashboard.setVariable(getDashName(thisRef, property), value as Any)
    }
}
