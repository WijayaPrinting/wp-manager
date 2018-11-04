package com.hendraanggrian.openpss.ui.invoice

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.content.PATTERN_DATE
import com.hendraanggrian.openpss.content.currencyConverter
import com.hendraanggrian.openpss.util.bold
import com.hendraanggrian.openpss.util.currencyCell
import com.hendraanggrian.openpss.util.numberCell
import com.hendraanggrian.openpss.util.stringCell
import com.hendraanggrian.openpss.db.dbDateTime
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.control.dialog.ResultableDialog
import com.hendraanggrian.openpss.control.popover.ResultablePopover
import com.hendraanggrian.openpss.ui.invoice.job.AddDigitalJobPopover
import com.hendraanggrian.openpss.ui.invoice.job.AddOffsetJobPopover
import com.hendraanggrian.openpss.ui.invoice.job.AddOtherJobPopover
import com.hendraanggrian.openpss.ui.invoice.job.AddPlateJobPopover
import com.hendraanggrian.openpss.util.getColor
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.HPos.RIGHT
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority.ALWAYS
import ktfx.NodeInvokable
import ktfx.application.later
import ktfx.beans.binding.`when`
import ktfx.beans.binding.buildDoubleBinding
import ktfx.beans.binding.buildStringBinding
import ktfx.beans.binding.otherwise
import ktfx.beans.binding.then
import ktfx.beans.value.greater
import ktfx.beans.value.lessEq
import ktfx.beans.value.or
import ktfx.collections.isEmpty
import ktfx.coroutines.onAction
import ktfx.coroutines.onKeyPressed
import ktfx.coroutines.onMouseClicked
import ktfx.jfoenix.jfxTabPane
import ktfx.jfoenix.jfxTextField
import ktfx.layouts.TableColumnsBuilder
import ktfx.layouts.columns
import ktfx.layouts.contextMenu
import ktfx.layouts.gridPane
import ktfx.layouts.label
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.tableView
import ktfx.layouts.textArea
import ktfx.scene.input.isDelete
import ktfx.scene.layout.gap
import ktfx.util.invoke
import org.joda.time.DateTime

class AddInvoiceDialog(
    context: Context
) : ResultableDialog<Invoice>(context, R.string.add_invoice) {

    private lateinit var offsetTable: TableView<Invoice.OffsetJob>
    private lateinit var digitalTable: TableView<Invoice.DigitalJob>
    private lateinit var plateTable: TableView<Invoice.PlateJob>
    private lateinit var otherTable: TableView<Invoice.OtherJob>
    private lateinit var noteArea: TextArea

    private val dateTime: DateTime = dbDateTime
    private val customerProperty: ObjectProperty<Customer> = SimpleObjectProperty(null)
    private val totalProperty: DoubleProperty = SimpleDoubleProperty()

    init {
        gridPane {
            gap = R.dimen.padding_medium.toDouble()
            label(getString(R.string.employee)) col 0 row 0
            label(login.name) { font = bold() } col 1 row 0
            label(getString(R.string.date)) col 2 row 0 hpriority ALWAYS halign RIGHT
            label(dateTime.toString(PATTERN_DATE)) { font = bold() } col 3 row 0
            label(getString(R.string.customer)) col 0 row 1
            jfxTextField {
                isEditable = false
                textProperty().bind(buildStringBinding(customerProperty) {
                    customerProperty.value?.toString() ?: getString(R.string.search_customer)
                })
                onMouseClicked {
                    SearchCustomerPopover(this@AddInvoiceDialog).show(this@jfxTextField) { customerProperty.set(it) }
                }
            } col 1 row 1
            label(getString(R.string.jobs)) col 0 row 2
            jfxTabPane {
                styleClass += "jfx-tab-pane-small"
                (getString(R.string.offset)) {
                    offsetTable = invoiceTableView({ AddOffsetJobPopover(this@AddInvoiceDialog) }) {
                        columns {
                            column<Invoice.OffsetJob, String>(R.string.qty, 72) { numberCell { qty } }
                            column<Invoice.OffsetJob, String>(R.string.machine, 72) { stringCell { type } }
                            column<Invoice.OffsetJob, String>(R.string.technique, 72) {
                                stringCell { typedTechnique.toString(this@AddInvoiceDialog) }
                            }
                            column<Invoice.OffsetJob, String>(R.string.title, 192) { stringCell { title } }
                            column<Invoice.OffsetJob, String>(R.string.total, 156) { currencyCell { total } }
                        }
                    }
                }
                (getString(R.string.digital)) {
                    digitalTable = invoiceTableView({ AddDigitalJobPopover(this@AddInvoiceDialog) }) {
                        columns {
                            column<Invoice.DigitalJob, String>(R.string.qty, 72) { numberCell { qty } }
                            column<Invoice.DigitalJob, String>(R.string.machine, 72) { stringCell { type } }
                            column<Invoice.DigitalJob, String>(R.string.title, 192) { stringCell { title } }
                            column<Invoice.DigitalJob, String>(R.string.total, 156) { currencyCell { total } }
                        }
                    }
                }
                (getString(R.string.plate)) {
                    plateTable = invoiceTableView({ AddPlateJobPopover(this@AddInvoiceDialog) }) {
                        columns {
                            column<Invoice.PlateJob, String>(R.string.qty, 72) { numberCell { qty } }
                            column<Invoice.PlateJob, String>(R.string.machine, 72) { stringCell { type } }
                            column<Invoice.PlateJob, String>(R.string.title, 264) { stringCell { title } }
                            column<Invoice.PlateJob, String>(R.string.total, 156) { currencyCell { total } }
                        }
                    }
                }
                (getString(R.string.others)) {
                    otherTable = invoiceTableView({ AddOtherJobPopover(this@AddInvoiceDialog) }) {
                        columns {
                            column<Invoice.OtherJob, String>(R.string.qty, 72) { numberCell { qty } }
                            column<Invoice.OtherJob, String>(R.string.title, 336) { stringCell { title } }
                            column<Invoice.OtherJob, String>(R.string.total, 156) { currencyCell { total } }
                        }
                    }
                }
            } col 1 row 2 colSpans 3
            totalProperty.bind(buildDoubleBinding(plateTable.items, offsetTable.items, otherTable.items) {
                plateTable.items.sumByDouble { it.total } +
                    offsetTable.items.sumByDouble { it.total } +
                    otherTable.items.sumByDouble { it.total }
            })
            label(getString(R.string.note)) col 0 row 3
            noteArea = textArea {
                prefHeight = 75.0
            } col 1 row 3 colSpans 3
            label(getString(R.string.total)) col 0 row 4
            label {
                font = bold()
                textProperty().bind(buildStringBinding(totalProperty) {
                    currencyConverter(totalProperty.value)
                })
                textFillProperty().bind(
                    `when`(totalProperty greater 0)
                        then getColor(R.color.green)
                        otherwise getColor(R.color.red)
                )
            } col 1 row 4
        }
        defaultButton.disableProperty().bind(customerProperty.isNull or totalProperty.lessEq(0))
    }

    override val nullableResult: Invoice?
        get() = null/*Invoice.new(
            login.id,
            customerProperty.value.id,
            dateTime,
            offsetTable.items,
            plateTable.items,
            otherTable.items,
            noteArea.text
        )*/

    private fun <S> NodeInvokable.invoiceTableView(
        newAddJobPopover: () -> ResultablePopover<S>,
        init: TableView<S>.() -> Unit
    ): TableView<S> = tableView {
        prefHeight = 200.0
        init()
        contextMenu {
            getString(R.string.add)(ImageView(R.image.menu_add)) {
                onAction { newAddJobPopover().show(this@tableView) { this@tableView.items.add(it) } }
            }
            separatorMenuItem()
            getString(R.string.delete)(ImageView(R.image.menu_delete)) {
                later { disableProperty().bind(this@tableView.selectionModel.selectedItemProperty().isNull) }
                onAction { this@tableView.items.remove(this@tableView.selectionModel.selectedItem) }
            }
            getString(R.string.clear)(ImageView(R.image.menu_clear)) {
                later { disableProperty().bind(this@tableView.items.isEmpty) }
                onAction { this@tableView.items.clear() }
            }
        }
        onKeyPressed {
            if (it.code.isDelete() && selectionModel.selectedItem != null) items.remove(selectionModel.selectedItem)
        }
    }

    private fun <S, T> TableColumnsBuilder<S>.column(
        textId: String,
        width: Int,
        init: TableColumn<S, T>.() -> Unit
    ): TableColumn<S, T> = column(getString(textId)) {
        width.toDouble().let {
            minWidth = it
            prefWidth = it
            maxWidth = it
        }
        init()
    }
}