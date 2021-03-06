package com.hendraanggrian.openpss.ui.wage

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.ui.BasePopOver
import com.jfoenix.controls.JFXButton
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Separator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ktfx.collections.mutableObservableListOf
import ktfx.controls.gap
import ktfx.coroutines.onAction
import ktfx.jfoenix.layouts.jfxButton
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.or

class DisableRecessPopOver(
    component: FxComponent,
    private val attendeePanes: List<AttendeePane>
) : BasePopOver(component, R2.string.disable_recess) {

    private val recessChoice: ComboBox<*>
    private val roleChoice: ComboBox<*>

    override val focusedNode: Node? get() = recessChoice

    init {
        gridPane {
            gap = getDouble(R.value.padding_medium)
            label(getString(R2.string.recess)) col 0 row 0
            recessChoice = jfxComboBox(
                mutableObservableListOf(
                    getString(R2.string.all),
                    Separator(),
                    *runBlocking(Dispatchers.IO) { OpenPSSApi.getRecesses() }.toTypedArray()
                )
            ) { selectionModel.selectFirst() } col 1 row 0
            label(getString(R2.string.employee)) col 0 row 1
            roleChoice = jfxComboBox(
                mutableObservableListOf(
                    *attendees.asSequence().filter { it.role != null }.map { it.role!! }.distinct().toList().toTypedArray(),
                    Separator(),
                    *attendees.toTypedArray()
                )
            ) col 1 row 1
        }
        buttonManager.run {
            jfxButton(getString(R2.string.apply)) {
                isDefaultButton = true
                buttonType = JFXButton.ButtonType.RAISED
                styleClass += R.style.raised
                disableProperty().bind(recessChoice.valueProperty().isNull or roleChoice.valueProperty().isNull)
                onAction {
                    attendeePanes
                        .asSequence()
                        .filter { pane ->
                            when (roleChoice.value) {
                                is String -> pane.attendee.role == roleChoice.value
                                else -> pane.attendee == roleChoice.value as Attendee
                            }
                        }.map { pane -> pane.recessChecks }
                        .toList()
                        .forEach { pane ->
                            (when (recessChoice.value) {
                                is String -> pane
                                else -> pane.filter { _pane -> _pane.text == recessChoice.value.toString() }
                            }).forEach { _pane ->
                                _pane.isSelected = false
                            }
                        }
                }
            }
        }
    }

    private inline val attendees: List<Attendee> get() = attendeePanes.map { it.attendee }
}
