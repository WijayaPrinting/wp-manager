package com.wijayaprinting.manager.internal

import com.wijayaprinting.manager.App
import java.net.URL
import java.util.*

/** Easy access to [ResourceBundle] in application and controllers. */
interface Resourceful {

    val resources: ResourceBundle

    fun getString(key: String): String = resources.getString(key)
    fun getBoolean(key: String): Boolean = getString(key).toBoolean()
    fun getInt(key: String): Int = getString(key).toInt()
    fun getLong(key: String): Long = getString(key).toLong()
    fun getFloat(key: String): Float = getString(key).toFloat()
    fun getDouble(key: String): Double = getString(key).toDouble()

    fun getResource(name: String): URL = App::class.java.getResource(name)
}