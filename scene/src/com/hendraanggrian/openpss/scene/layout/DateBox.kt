@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.layout

import com.hendraanggrian.openpss.scene.R
import com.hendraanggrian.openpss.time.toJava
import com.hendraanggrian.openpss.time.toJoda
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos.CENTER
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import javafx.scene.image.ImageView
import ktfx.beans.binding.bindingOf
import ktfx.beans.value.getValue
import ktfx.coroutines.onAction
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import ktfx.layouts._HBox
import ktfx.layouts.button
import ktfx.layouts.datePicker
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now

/**
 * A [DatePicker] that always has a valid value.
 *
 * [DateBox] width is deliberately measured to match [com.hendraanggrian.scene.layout.TimeBox]'s width.
 */
open class DateBox(prefill: LocalDate = now()) : _HBox() {

    lateinit var picker: DatePicker
    var previousButton: Button
    var nextButton: Button

    val dateProperty: ObjectProperty<LocalDate> = SimpleObjectProperty()
    val date: LocalDate by dateProperty

    init {
        alignment = CENTER
        spacing = 8.0

        previousButton = button(graphic = ImageView(R.image.btn_previous)) {
            onAction { picker.value = picker.value.minusDays(1) }
        }
        picker = datePicker {
            value = prefill.toJava()
            isEditable = false
            maxWidth = 116.0
        }
        dateProperty.bind(bindingOf(picker.valueProperty()) { picker.value.toJoda() })
        nextButton = button(graphic = ImageView(R.image.btn_next)) {
            onAction { picker.value = picker.value.plusDays(1) }
        }
    }
}

inline fun dateBox(
    prefill: LocalDate = now(),
    noinline init: ((@LayoutDsl DateBox).() -> Unit)? = null
): DateBox = DateBox(prefill).apply { init?.invoke(this) }

inline fun LayoutManager<Node>.dateBox(
    prefill: LocalDate = now(),
    noinline init: ((@LayoutDsl DateBox).() -> Unit)? = null
): DateBox = com.hendraanggrian.openpss.scene.layout.dateBox(prefill, init).add()