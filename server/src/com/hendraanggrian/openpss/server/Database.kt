package com.hendraanggrian.openpss.server

import com.hendraanggrian.openpss.BuildConfig
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.DigitalPrices
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.GlobalSettings
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.db.schemas.OffsetPrices
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.schemas.PlatePrices
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.db.schemas.Wages
import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential
import com.mongodb.MongoException
import com.mongodb.ServerAddress
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.mongodb.MongoDB
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import java.util.Date

private lateinit var DATABASE: MongoDB
val TABLES: Array<DocumentSchema<out Document<out DocumentSchema<out Document<out DocumentSchema<*>>>>>> = arrayOf(
    Customers,
    DigitalPrices,
    Employees,
    Logs,
    GlobalSettings,
    Invoices,
    OffsetPrices,
    Payments,
    PlatePrices,
    Recesses,
    Wages
)

/**
 * A failed transaction will most likely throw an exception instance of [MongoException].
 * This function will safely execute a transaction and display an error log on JavaFX if it throws those exceptions.
 *
 * @see [MongoDB.withSession]
 */
@Throws(MongoException::class)
fun <T> transaction(statement: SessionWrapper.() -> T): T = try {
    DATABASE.withSession {
        SessionWrapper(
            this
        ).statement()
    }
} catch (e: MongoException) {
    error("Connection closed. Please sign in again.")
}

fun connect() {
    DATABASE = MongoDB(
        arrayOf(ServerAddress(ServerAddress.defaultHost(), ServerAddress.defaultPort())),
        BuildConfig.DATABASE,
        arrayOf(
            MongoCredential.createCredential(
                BuildConfig.DATABASE_USERNAME,
                "admin",
                BuildConfig.DATABASE_PASSWORD.toCharArray()
            )
        ),
        MongoClientOptions.Builder().serverSelectionTimeout(3000).build(),
        TABLES
    )
}

/** Date and time of server. */
fun dbDateTime(): DateTime = DateTime(evalDate)

/** Local date of server. */
fun dbDate(): LocalDate = LocalDate.fromDateFields(evalDate)

/** Local time of server. */
fun dbTime(): LocalTime = LocalTime.fromDateFields(evalDate)

@Suppress("DEPRECATION")
private inline val evalDate: Date
    get() = DATABASE.db.doEval("new Date()").getDate("retval")