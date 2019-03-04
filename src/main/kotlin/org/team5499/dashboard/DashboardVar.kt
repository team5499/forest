package org.team5499.dashboard

import kotlin.reflect.KProperty
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.KVisibility
import kotlin.reflect.KMutableProperty

class DashboardVar<T>(val initValue: T) {

    companion object {
        @SuppressWarnings("EmptyCatchBlock", "TooGenericExceptionCaught")
        fun initClassProps(clazz: KClass<*>) {
            clazz.memberProperties.forEach({
                if (it.visibility == KVisibility.PUBLIC && it is KMutableProperty<*>) {
                    try {
                        it.getter.call(clazz.objectInstance)
                    } catch (e: Exception) {
                    }
                }
            })
            clazz.nestedClasses.forEach({
                initClassProps(it)
            })
        }
    }

    var dashName = ""

    fun getDashName(thisRef: Any?, property: KProperty<*>): String {
        val packageLength = thisRef!!::class.java.getPackage().name.length
        return "${thisRef::class.qualifiedName!!.substring(packageLength + 1)}.${property.name}"
    }

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): DashboardVar<T> {
        dashName = getDashName(thisRef, property)
        Dashboard.setVariable(dashName, initValue as Any)
        return this
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return Dashboard.getVariable(dashName)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        Dashboard.setVariable(dashName, value as Any)
    }
}
