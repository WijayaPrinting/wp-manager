@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.hendraanggrian.openpss.FxComponent
import javafx.scene.control.TreeTableColumn
import ktfx.asFinalProperty
import ktfx.invoke

fun <T> TreeTableColumn<T, String>.stringCell(target: T.() -> Any) = setCellValueFactory { col ->
    col.value.value.target().let { it as? String ?: it.toString() }.asFinalProperty()
}

fun <T> TreeTableColumn<T, String>.numberCell(component: FxComponent, target: T.() -> Int) {
    style = "-fx-alignment: center-right;"
    setCellValueFactory { component.numberConverter(it.value.value.target()).asFinalProperty() }
}
