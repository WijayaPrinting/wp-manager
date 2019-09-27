package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.nosql.NamedDocument
import com.hendraanggrian.openpss.nosql.StringId
import com.hendraanggrian.openpss.schema.DigitalPrices

data class DigitalPrice(
    override var name: String,
    var oneSidePrice: Double,
    var twoSidePrice: Double
) : NamedDocument<DigitalPrices> {

    companion object {

        fun new(name: String): DigitalPrice = DigitalPrice(name, 0.0, 0.0)
    }

    override lateinit var id: StringId<DigitalPrices>

    override fun toString(): String = name
}
