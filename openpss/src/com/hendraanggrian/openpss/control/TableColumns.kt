@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.numberConverter
import javafx.geometry.Pos.CENTER
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.control.TableColumn
import javafx.scene.image.Image
import ktfx.beans.property.toProperty
import ktfx.layouts.imageView
import ktfx.listeners.cellFactory
import ktfx.styles.labeledStyle
import ktfx.util.invoke

fun <T> TableColumn<T, Boolean>.doneCell(size: Int = 64, target: T.() -> Boolean) {
    size.toDouble().let {
        minWidth = it
        prefWidth = it
        maxWidth = it
    }
    isResizable = false
    style = labeledStyle { alignment = CENTER }
    setCellValueFactory { it.value.target().toProperty() }
    cellFactory {
        onUpdate { done, empty ->
            text = null
            graphic = null
            if (done != null && !empty) graphic = imageView(
                Image(
                    when {
                        done -> R.image.btn_done_yes
                        else -> R.image.btn_done_no
                    }
                )
            )
        }
    }
}

fun <T> TableColumn<T, String>.stringCell(target: T.() -> String?) =
    setCellValueFactory { it.value.target().orEmpty().toProperty() }

fun <T> TableColumn<T, String>.numberCell(target: T.() -> Int) {
    style = labeledStyle { alignment = CENTER_RIGHT }
    setCellValueFactory { numberConverter(it.value.target()).toProperty() }
}

fun <T> TableColumn<T, String>.currencyCell(target: T.() -> Double) {
    style = labeledStyle { alignment = CENTER_RIGHT }
    setCellValueFactory { currencyConverter(it.value.target()).toProperty() }
}