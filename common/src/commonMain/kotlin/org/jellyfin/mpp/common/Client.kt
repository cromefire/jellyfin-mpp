package org.jellyfin.mpp.common

import io.ktor.client.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.defaultSerializer
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.stringify

class JellyfinApi(
    //val appStorage: Any,
    val serverAddress: String,
    val platform: Platform,
    val devicePixelRatio: Int
) {
    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    private fun path(p: String): Url {
        return URLBuilder(serverAddress).path("jellyfin", p).build()
    }

    fun HttpRequestBuilder.auth(withToken: Boolean = true) {
        val params = HashMap<String, String>()
        params["Client"] = platform.client
        params["Device"] = platform.deviceName
        params["DeviceId"] = platform.deviceId
        params["Version"] = platform.clientVersion
        if (withToken) {
            params["Token"] = "<TODO>"
        }
        val paramsStr = params.map { e ->
            "${e.key}=${e.value}"
        }.joinToString(", ")
        header("X-Emby-Authorization", "MediaBrowser $paramsStr")
    }

    suspend fun authenticateUserByName(name: String, password: String): AuthenticateResponse {
        println(Json.stringify(AuthenticateRequest.serializer(), AuthenticateRequest(name, password)))

        return client.post(body = AuthenticateRequest(name, password)) {
            auth(withToken = false)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            url(path("users/authenticatebyname"))
        }
    }
}
