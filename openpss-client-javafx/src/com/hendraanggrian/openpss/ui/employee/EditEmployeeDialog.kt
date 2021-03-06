package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.api.OpenPSSApi
import com.hendraanggrian.openpss.schema.Employee
import com.hendraanggrian.openpss.ui.TableDialog
import com.hendraanggrian.openpss.util.clean
import com.hendraanggrian.openpss.util.doneCell
import com.hendraanggrian.openpss.util.stringCell
import javafx.scene.control.MenuItem
import javafx.scene.image.ImageView
import kotlinx.coroutines.CoroutineScope
import ktfx.controls.isSelected
import ktfx.controls.notSelectedBinding
import ktfx.coroutines.onAction
import ktfx.jfoenix.controls.jfxSnackbar
import ktfx.layouts.contextMenu
import ktfx.layouts.menuItem
import ktfx.layouts.separatorMenuItem
import ktfx.toBinding
import ktfx.toStringBinding

class EditEmployeeDialog(component: FxComponent) :
    TableDialog<Employee>(component, R2.string.employee, true) {

    init {
        getString(R2.string.name)<String> {
            stringCell { name }
        }
        getString(R2.string.admin)<Boolean> {
            doneCell { isAdmin }
        }
        table.contextMenu {
            menuItem {
                textProperty().bind(table.selectionModel.selectedItemProperty().toStringBinding {
                    when {
                        table.selectionModel.isSelected() -> getString(
                            when {
                                it!!.isAdmin -> R2.string.disable_admin_status
                                else -> R2.string.enable_admin_status
                            }
                        )
                        else -> null
                    }
                })
                graphicProperty().bind(table.selectionModel.selectedItemProperty().toBinding {
                    when {
                        table.selectionModel.isSelected() -> ImageView(
                            when {
                                it!!.isAdmin -> R.image.menu_admin_off
                                else -> R.image.menu_admin_on
                            }
                        )
                        else -> null
                    }
                })
                bindDisable()
                onAction {
                    val selected = table.selectionModel.selectedItem
                    OpenPSSApi.editEmployee(selected.apply { isAdmin = !isAdmin }, login.name)
                    refresh()
                }
            }
            separatorMenuItem()
            (getString(R2.string.reset_password)) {
                bindDisable()
                onAction {
                    val selected = table.selectionModel.selectedItem
                    OpenPSSApi.editEmployee(
                        selected.apply { password = Employee.DEFAULT_PASSWORD },
                        login.name
                    )
                    rootLayout.jfxSnackbar(
                        getString(
                            R2.string.change_password_popup_will_appear_when_is_logged_back_in,
                            login.name
                        ),
                        getLong(R.value.duration_long)
                    )
                }
            }
        }
    }

    override suspend fun CoroutineScope.refresh(): List<Employee> = OpenPSSApi.getEmployees()

    override fun add() =
        AddEmployeePopOver(this, R2.string.add_employee, false).show(addButton) { name ->
            val added = OpenPSSApi.addEmployee(Employee.new(name!!.clean()))
            table.items.add(added)
            table.selectionModel.select(added)
        }

    override suspend fun CoroutineScope.delete(selected: Employee): Boolean =
        OpenPSSApi.deleteEmployee(login, selected.id)

    private fun MenuItem.bindDisable() =
        disableProperty().bind(table.selectionModel.notSelectedBinding)
}
