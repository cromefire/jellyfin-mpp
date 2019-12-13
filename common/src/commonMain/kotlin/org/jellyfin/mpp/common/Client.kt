package org.jellyfin.mpp.common

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class JellyfinApi(
    //val appStorage: Any,
    val serverAddress: String,
    val platform: Platform,
    val devicePixelRatio: Int
) {
    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json.nonstrict)
        }
    }

    private fun path(p: String): Url {
        return URLBuilder(serverAddress).path(Url(serverAddress).encodedPath, p).build()
    }

    fun HttpRequestBuilder.auth(withToken: Boolean = true) {
        val params = ArrayList<String>()
        params.add("Client=\"${platform.client}\"")
        params.add("Device=\"${platform.deviceName}\"")
        params.add("DeviceId=\"${platform.deviceId}\"")
        params.add("Version=\"${platform.clientVersion}\"")
        if (withToken) {
            params.add("Token <TODO>")
        }
        header("X-Emby-Authorization", "MediaBrowser ${params.joinToString(", ")}")
    }

    suspend fun authenticateUserByName(name: String, password: String): AuthenticateResponse {
        return client.post(body = AuthenticateRequest(name, password)) {
            auth(withToken = false)
            contentType(ContentType.Application.Json)
            url(path("Users/AuthenticateByName"))
        }
    }
}
