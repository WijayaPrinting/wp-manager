package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.ui.FxComponent
import com.hendraanggrian.openpss.ui.TableDialog
import com.hendraanggrian.openpss.util.clean
import com.hendraanggrian.openpss.util.doneCell
import com.hendraanggrian.openpss.util.stringCell
import javafx.scene.control.MenuItem
import kotlinx.coroutines.CoroutineScope
import ktfx.bindings.buildBinding
import ktfx.bindings.buildStringBinding
import ktfx.controls.isSelected
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxSnackbar
import ktfx.layouts.contextMenu
import ktfx.layouts.menuItem
import ktfx.layouts.separatorMenuItem

class EditEmployeeDialog(component: FxComponent) :
    TableDialog<Employee>(component, R.string.employee, true) {

    init {
        getString(R.string.name)<String> {
            stringCell { name }
        }
        getString(R.string.admin)<Boolean> {
            doneCell { isAdmin }
        }
        table.contextMenu {
            menuItem {
                textProperty().bind(buildStringBinding(table.selectionModel.selectedIndexProperty()) {
                    when {
                        table.selectionModel.isSelected() -> getString(
                            when {
                                table.selectionModel.selectedItem.isAdmin -> R.string.disable_admin_status
                                else -> R.string.enable_admin_status
                            }
                        )
                        else -> null
                    }
                })
                graphicProperty().bind(buildBinding(table.selectionModel.selectedIndexProperty()) {
                    when {
                        table.selectionModel.isSelected() -> ktfx.layouts.imageView(
                            when {
                                table.selectionModel.selectedItem.isAdmin -> R.image.menu_admin_off
                                else -> R.image.menu_admin_on
                            }
                        )
                        else -> null
                    }
                })
                bindDisable()
                onAction {
                    val selected = table.selectionModel.selectedItem
                    api.editEmployee(selected.apply { isAdmin = !isAdmin }, login.name)
                    refresh()
                }
            }
            separatorMenuItem()
            (getString(R.string.reset_password)) {
                bindDisable()
                onAction {
                    val selected = table.selectionModel.selectedItem
                    api.editEmployee(
                        selected.apply { password = Employee.DEFAULT_PASSWORD },
                        login.name
                    )
                    rootLayout.jfxSnackbar(
                        getString(
                            R.string.change_password_popup_will_appear_when_is_logged_back_in,
                            login.name
                        ),
                        getLong(R.value.duration_long)
                    )
                }
            }
        }
    }

    override suspend fun CoroutineScope.refresh(): List<Employee> = api.getEmployees()

    override fun add() =
        AddEmployeePopOver(this, R.string.add_employee, false).show(addButton) { name ->
            val added = api.addEmployee(Employee.new(name!!.clean()))
            table.items.add(added)
            table.selectionModel.select(added)
        }

    override suspend fun CoroutineScope.delete(selected: Employee): Boolean =
        api.deleteEmployee(login, selected.id)

    private fun MenuItem.bindDisable() =
        disableProperty().bind(table.selectionModel.selectedItemProperty().isNull)
}