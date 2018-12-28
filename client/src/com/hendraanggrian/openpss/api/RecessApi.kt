package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.Recess
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import kotlinx.nosql.Id
import org.joda.time.LocalTime

interface RecessApi : Api {

    suspend fun getRecesses(): List<Recess> = client.get {
        apiUrl("recesses")
    }

    suspend fun addRecess(start: LocalTime, end: LocalTime): Recess = client.post {
        apiUrl("recesses")
        parameters(
            "start" to start,
            "end" to end
        )
    }

    suspend fun deleteRecess(id: Id<String, *>): Boolean = client.requestStatus(HttpMethod.Delete) {
        apiUrl("recesses/$id")
    }
}