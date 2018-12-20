package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.data.Release
import com.hendraanggrian.openpss.internal.ClientBuildConfig
import com.hendraanggrian.openpss.util.jodaTimeSupport
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.response.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom

interface Api {

    val endPoint: String

    val client: HttpClient

    fun HttpRequestBuilder.json() = contentType(ContentType.Application.Json)

    fun HttpRequestBuilder.apiUrl(path: String) {
        header(HttpHeaders.CacheControl, "no-cache")
        url {
            takeFrom(endPoint)
            encodedPath = path
        }
    }

    fun HttpRequestBuilder.parameters(vararg pairs: Pair<String, Any?>) =
        pairs.forEach { (key, value) -> parameter(key, value) }

    suspend fun HttpClient.requestStatus(block: HttpRequestBuilder.() -> Unit): Boolean =
        request<HttpResponse>(block).use { it.status.isSuccess() }
}

/** Base class of REST APIs, where client is Android and Java-friendly OkHttp. */
sealed class OkHttpApi(final override val endPoint: String) : Api {

    final override val client: HttpClient = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = GsonSerializer { jodaTimeSupport() }
        }
    }
}

/** GitHub API used to check latest version. */
class GitHubApi : OkHttpApi("https://api.github.com") {

    suspend fun getLatestRelease(): Release = client.get {
        apiUrl("repos/${ClientBuildConfig.USER}/${ClientBuildConfig.ARTIFACT}/releases/latest")
    }
}

/** Main API. */
class OpenPSSApi : OkHttpApi("http://localhost:8080"),
    AuthApi,
    CustomerApi,
    DateTimeApi,
    GlobalSettingApi,
    InvoiceApi,
    LogApi,
    NamedApi,
    PaymentApi,
    RecessApi,
    WageApi