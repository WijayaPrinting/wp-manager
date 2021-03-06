package com.hendraanggrian.openpss.nosql

import javafx.util.Builder
import kotlinx.nosql.AndQuery
import kotlinx.nosql.Query
import kotlinx.nosql.query.NoQuery
import kotlinx.nosql.query.OrQuery

typealias DocumentQuery<T, P, C> = kotlinx.nosql.DocumentSchemaQueryWrapper<T, P, C>

interface QueryBuilder {

    fun and(target: Query)

    fun or(target: Query)
}

class QueryBuilderImpl : QueryBuilder, Builder<Query> {
    private var source: Query? = null

    override fun and(target: Query): Unit = setOrCreate(target) { AndQuery(it, target) }

    override fun or(target: Query): Unit = setOrCreate(target) { OrQuery(it, target) }

    override fun build(): Query = source ?: NoQuery

    private fun setOrCreate(target: Query, creator: (Query) -> Query) {
        source = source?.let { creator(it) } ?: target
    }
}
