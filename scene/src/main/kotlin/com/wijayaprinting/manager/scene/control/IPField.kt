@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.manager.scene.control

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TextField
import kotfx.*
import org.apache.commons.validator.routines.InetAddressValidator.getInstance

open class IPField : TextField() {

    val validProperty = SimpleBooleanProperty()

    init {
        validProperty bind booleanBindingOf(textProperty()) { getInstance().isValidInet4Address(text) }
    }

    val isValid: Boolean get() = validProperty.value
}

@JvmOverloads inline fun ipField(noinline init: ((@KotfxDsl IPField).() -> Unit)? = null): IPField = IPField().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.ipField(noinline init: ((@KotfxDsl IPField).() -> Unit)? = null): IPField = IPField().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.ipField(noinline init: ((@KotfxDsl IPField).() -> Unit)? = null): IPField = IPField().apply { init?.invoke(this) }.add()