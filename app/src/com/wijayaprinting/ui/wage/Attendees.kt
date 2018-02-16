package com.wijayaprinting.ui.wage

import com.wijayaprinting.scene.START_OF_TIME
import com.wijayaprinting.ui.Resourced
import com.wijayaprinting.ui.wage.Record.Companion.INDEX_NODE
import com.wijayaprinting.ui.wage.Record.Companion.INDEX_TOTAL
import com.wijayaprinting.util.round
import kotfx.bindings.doubleBindingOf
import kotfx.properties.toProperty
import org.joda.time.DateTime.now

fun Attendee.toNodeRecord(resourced: Resourced): Record = Record(resourced, INDEX_NODE, this, now().toProperty(), now().toProperty())

fun Attendee.toChildRecords(resourced: Resourced): Set<Record> {
    val records = mutableSetOf<Record>()
    val iterator = attendances.iterator()
    var index = 0
    while (iterator.hasNext()) records.add(Record(resourced, index++, this, iterator.next().toProperty(), iterator.next().toProperty()))
    return records
}

fun Attendee.toTotalRecords(resourced: Resourced, children: Collection<Record>): Record = Record(resourced, INDEX_TOTAL, this, START_OF_TIME.toProperty(), START_OF_TIME.toProperty()).apply {
    children.map { it.dailyProperty }.toTypedArray().let { mains -> dailyProperty.bind(doubleBindingOf(*mains) { round(mains.map { it.value }.sum()) }) }
    children.map { it.dailyIncomeProperty }.toTypedArray().let { mainIncomes -> dailyIncomeProperty.bind(doubleBindingOf(*mainIncomes) { round(mainIncomes.map { it.value }.sum()) }) }
    children.map { it.overtimeProperty }.toTypedArray().let { overtimes -> overtimeProperty.bind(doubleBindingOf(*overtimes) { round(overtimes.map { it.value }.sum()) }) }
    children.map { it.overtimeIncomeProperty }.toTypedArray().let { overtimeIncomes -> overtimeIncomeProperty.bind(doubleBindingOf(*overtimeIncomes) { round(overtimeIncomes.map { it.value }.sum()) }) }
    children.map { it.totalProperty }.toTypedArray().let { totals -> totalProperty.bind(doubleBindingOf(*totals) { round(totals.map { it.value }.sum()) }) }
}