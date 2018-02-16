package com.wijayaprinting.db.dao

import com.wijayaprinting.db.Ided
import com.wijayaprinting.db.schema.Recesses
import com.wijayaprinting.scene.PATTERN_TIME
import kotlinx.nosql.Id
import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.LocalTime

data class Recess(
    var start: LocalTime,
    var end: LocalTime
) : Ided<Recesses> {
    override lateinit var id: Id<String, Recesses>

    override fun toString(): String = "${start.toString(PATTERN_TIME)} - ${end.toString(PATTERN_TIME)}"

    fun getInterval(dateTime: DateTime): Interval = Interval(start.toDateTime(dateTime), end.toDateTime(dateTime))
}