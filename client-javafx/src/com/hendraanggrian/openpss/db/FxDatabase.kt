package com.hendraanggrian.openpss.db

import com.hendraanggrian.openpss.App
import ktfx.scene.control.errorAlert

fun <T> transaction(statement: SessionWrapper.() -> T): T = try {
    Database.withSession(statement)
} catch (e: IllegalStateException) {
    errorAlert(e.message.toString()) {
        setOnCloseRequest {
            App.exit()
        }
    }.showAndWait()
    throw RuntimeException()
}