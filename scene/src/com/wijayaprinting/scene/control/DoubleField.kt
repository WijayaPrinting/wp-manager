@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.scene.control

import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.control.TextField
import kotfx.annotations.SceneDsl
import kotfx.bindings.booleanBindingOf
import kotfx.scene.ChildManager
import kotfx.scene.ItemManager
import kotfx.stringConverterOf

open class DoubleField : TextField() {

    val valueProperty: DoubleProperty = SimpleDoubleProperty()
    val validProperty: BooleanProperty = SimpleBooleanProperty()

    init {
        textProperty().bindBidirectional(valueProperty, stringConverterOf<Number> { it.toDoubleOrNull() ?: 0.0 })
        validProperty.bind(booleanBindingOf(textProperty()) {
            try {
                java.lang.Double.parseDouble(text)
                true
            } catch (e: NumberFormatException) {
                false
            }
        })
    }

    var value: Double
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)

    val isValid: Boolean get() = validProperty.get()
}

inline fun doubleField(noinline init: ((@SceneDsl DoubleField).() -> Unit)? = null): DoubleField = DoubleField().apply { init?.invoke(this) }
inline fun ChildManager.doubleField(noinline init: ((@SceneDsl DoubleField).() -> Unit)? = null): DoubleField = DoubleField().apply { init?.invoke(this) }.add()
inline fun ItemManager.doubleField(noinline init: ((@SceneDsl DoubleField).() -> Unit)? = null): DoubleField = DoubleField().apply { init?.invoke(this) }.add()