package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.resources.Resourced
import com.hendraanggrian.openpss.util.clean
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.util.isName
import javafx.scene.control.ButtonBar.ButtonData.OK_DONE
import javafx.scene.control.ButtonType.NO
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.ButtonType.YES
import javafx.scene.control.TextInputDialog
import javafx.scene.image.ImageView
import ktfx.beans.binding.booleanBindingOf
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.styledWarningAlert

class UserDialog(
    resourced: Resourced,
    headerId: String,
    graphicId: String,
    prefill: String = ""
) : TextInputDialog(), Resourced by resourced {

    init {
        headerTitle = getString(headerId)
        graphicIcon = ImageView(graphicId)
        contentText = getString(R.string.name)
        editor.text = prefill
        dialogPane.run {
            stylesheets += getStyle(R.style.openpss)
            lookupButton(OK).disableProperty().bind(booleanBindingOf(editor.textProperty()) { !editor.text.isName() })
        }
        setResultConverter {
            if (it.buttonData != OK_DONE) return@setResultConverter null
            val name = editor.text.clean()
            if (name.split(" ").any { it.firstOrNull().let { it == null || it.isLowerCase() } })
                return@setResultConverter when
                (styledWarningAlert(getStyle(R.style.openpss),
                    getString(R.string.name_doesnt_start_with_uppercase_letter_add_anyway), YES, NO)
                    .showAndWait().get()) {
                    YES -> name
                    else -> null
                }
            name
        }
    }
}