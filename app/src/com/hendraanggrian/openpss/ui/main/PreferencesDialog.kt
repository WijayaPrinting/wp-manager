package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.controls.RegionBox
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_COUNTRY
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_INVOICE_HEADERS
import com.hendraanggrian.openpss.db.schemas.GlobalSetting.Companion.KEY_LANGUAGE
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.internationalization.Region
import com.hendraanggrian.openpss.internationalization.Resourced
import com.hendraanggrian.openpss.io.properties.PreferencesFile
import com.hendraanggrian.openpss.io.properties.PreferencesFile.INVOICE_QUICK_SELECT_CUSTOMER
import com.hendraanggrian.openpss.io.properties.PreferencesFile.WAGE_READER
import com.hendraanggrian.openpss.ui.wage.readers.Reader
import com.hendraanggrian.openpss.util.clearConverters
import com.hendraanggrian.openpss.util.getColor
import com.hendraanggrian.openpss.util.getFont
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.util.onActionFilter
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.Node
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Dialog
import javafx.scene.control.TextArea
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import kotlinx.nosql.update
import ktfx.beans.binding.bindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.beans.property.toProperty
import ktfx.beans.value.and
import ktfx.coroutines.listener
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import ktfx.layouts._HBox
import ktfx.layouts._VBox
import ktfx.layouts.checkBox
import ktfx.layouts.choiceBox
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.textArea
import ktfx.layouts.vbox
import ktfx.scene.control.cancelButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.okButton
import ktfx.scene.layout.gap
import java.util.Currency

class PreferencesDialog(resourced: Resourced, showGlobalSettings: Boolean) : Dialog<Nothing>(), Resourced by resourced {

    private companion object {
        const val CURRENCY_INVALID = "-"
        const val INVOICE_HEADERS_DIVIDER = "|"
    }

    private var isLocalChanged = false.toProperty()
    private var isGlobalChanged = false.toProperty()

    private lateinit var invoiceHeadersArea: TextArea
    private lateinit var wageReaderChoice: ChoiceBox<Any>

    private lateinit var regionBox: RegionBox

    init {
        headerTitle = getString(R.string.preferences)
        graphicIcon = ImageView(R.image.header_preferences)
        dialogPane.run {
            stylesheets += getStyle(R.style.openpss)
            content = vbox {
                spacing = 16.0
                group(R.string.invoice) {
                    checkBox(getString(R.string.quick_select_customer_when_adding_invoice)) {
                        isSelected = PreferencesFile.INVOICE_QUICK_SELECT_CUSTOMER
                        selectedProperty().listener { _, _, value ->
                            isLocalChanged.set(true)
                            INVOICE_QUICK_SELECT_CUSTOMER = value
                        }
                    }
                }
                group(R.string.wage) {
                    item {
                        label(getString(R.string.reader))
                        wageReaderChoice = choiceBox(Reader.listAll()) {
                            value = Reader.of(WAGE_READER)
                            valueProperty().listener { _, _, value ->
                                isLocalChanged.set(true)
                                WAGE_READER = (value as Reader).name
                            }
                        }
                    }
                }
            }
            if (showGlobalSettings) expandableContent = group(R.string.global_settings) {
                gridPane {
                    gap = 8.0
                    transaction {
                        label(getString(R.string.currency)) row 0 col 0
                        regionBox = RegionBox(Region.from(
                            findGlobalSettings(KEY_LANGUAGE).single().value,
                            findGlobalSettings(KEY_COUNTRY).single().value
                        )).apply { valueProperty().listener { isGlobalChanged.set(true) } }.add() row 0 col 1
                        label {
                            font = getFont(R.font.sf_pro_text_bold)
                            textProperty().bind(stringBindingOf(regionBox.valueProperty()) {
                                try {
                                    Currency.getInstance(regionBox.value.toLocale()).symbol
                                } catch (e: Exception) {
                                    CURRENCY_INVALID
                                }
                            })
                            textFillProperty().bind(bindingOf(textProperty()) {
                                getColor(if (text == CURRENCY_INVALID) R.color.red else R.color.teal)
                            })
                        } row 0 col 2
                        label(getString(R.string.invoice_headers)) row 1 col 0
                        invoiceHeadersArea = textArea(findGlobalSettings(KEY_INVOICE_HEADERS).single().valueList
                            .joinToString("\n").trim()) {
                            setMaxSize(256.0, 88.0)
                            textProperty().listener { _, oldValue, value ->
                                when (INVOICE_HEADERS_DIVIDER) {
                                    in value -> text = oldValue
                                    else -> isGlobalChanged.set(true)
                                }
                            }
                        } row 1 col 1 colSpans 2
                    }
                }
            }
        }
        cancelButton()
        okButton {
            disableProperty().bind(!isLocalChanged and !isGlobalChanged)
            onActionFilter {
                if (isLocalChanged.value) PreferencesFile.save()
                if (isGlobalChanged.value) transaction {
                    findGlobalSettings(KEY_LANGUAGE).projection { value }.update(regionBox.value.language)
                    findGlobalSettings(KEY_COUNTRY).projection { value }.update(regionBox.value.country)
                    findGlobalSettings(KEY_INVOICE_HEADERS).projection { value }
                        .update(invoiceHeadersArea.text.trim().replace("\n", "|"))
                    clearConverters()
                }
                close()
            }
        }
    }

    private fun group(
        titleId: String,
        init: (@LayoutDsl _VBox).() -> Unit
    ): VBox = vbox(4.0) {
        label(getString(titleId)) { font = getFont(R.font.sf_pro_text_bold) }
        init()
    }

    private fun LayoutManager<Node>.group(
        titleId: String,
        init: (@LayoutDsl _VBox).() -> Unit
    ): VBox = vbox(4.0) {
        label(getString(titleId)) { font = getFont(R.font.sf_pro_text_bold) }
        init()
    }

    private fun LayoutManager<Node>.item(
        labelId: String? = null,
        init: (@LayoutDsl _HBox).() -> Unit
    ): HBox = hbox(8.0) {
        alignment = CENTER_LEFT
        if (labelId != null) label(getString(labelId))
        init()
    }
}