@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.layout

import com.hendraanggrian.openpss.scene.R
import com.hendraanggrian.openpss.time.toJava
import javafx.geometry.Pos.CENTER
import javafx.scene.control.DatePicker
import javafx.scene.image.ImageView
import kfx.coroutines.onAction
import kfx.layouts.ChildManager
import kfx.layouts.ItemManager
import kfx.layouts.LayoutDsl
import kfx.layouts._HBox
import kfx.layouts.button
import kfx.layouts.datePicker
import kfx.scene.layout.maxSize
import kfx.scene.layout.spacings
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now

/**
 * A [DatePicker] that always has a valid value.
 *
 * [DateBox] width is deliberately measured to match [com.hendraanggrian.scene.layout.TimeBox]'s width.
 */
open class DateBox(prefill: LocalDate = now()) : _HBox() {

    lateinit var picker: DatePicker

    init {
        alignment = CENTER
        spacings = 8

        button(graphic = ImageView(R.image.btn_arrow_left)) { onAction { picker.value = picker.value.minusDays(1) } }
        picker = datePicker {
            value = prefill.toJava()
            isEditable = false
            maxSize(width = 116)
        }
        button(graphic = ImageView(R.image.btn_arrow_right)) { onAction { picker.value = picker.value.plusDays(1) } }
    }
}

inline fun dateBox(prefill: LocalDate = now(), noinline init: ((@LayoutDsl DateBox).() -> Unit)? = null): DateBox = DateBox(prefill).apply { init?.invoke(this) }
inline fun ChildManager.dateBox(prefill: LocalDate = now(), noinline init: ((@LayoutDsl DateBox).() -> Unit)? = null): DateBox = DateBox(prefill).apply { init?.invoke(this) }.add()
inline fun ItemManager.dateBox(prefill: LocalDate = now(), noinline init: ((@LayoutDsl DateBox).() -> Unit)? = null): DateBox = DateBox(prefill).apply { init?.invoke(this) }.add()
