package org.jellyfin.mpp.common

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.contentType
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

private fun HttpRequestBuilder.addAuthToken(platform: Platform, token: String?) {
    val params = ArrayList<String>()
    params.add("Client=\"${platform.client}\"")
    params.add("Device=\"${platform.deviceName}\"")
    params.add("DeviceId=\"${platform.deviceId}\"")
    params.add("Version=\"${platform.clientVersion}\"")
    if (token != null) {
        params.add("Token=\"$token\"")
    }
    header("X-Emby-Authorization", "MediaBrowser ${params.joinToString(", ")}")
}

private fun HttpRequestBuilder.addPath(address: String, p: String) {
    this.url(URLBuilder(address).path(Url(address).encodedPath, p).build())
}

@UseExperimental(UnstableDefault::class)
suspend fun authenticateUserByName(
    platform: Platform,
    address: String,
    name: String,
    password: String
): AuthenticateResponse {
    val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json.nonstrict)
        }
    }
    return client.post(body = AuthenticateRequest(name, password)) {
        addAuthToken(platform, null)
        contentType(ContentType.Application.Json)
        addPath(address, "Users/AuthenticateByName")
    }
}

val defaultJson = Json(JsonConfiguration.Stable.copy(
    strictMode = false
))

class JellyfinClient(
    //val appStorage: Any,
    val serverAddress: String,
    val platform: Platform,
    val token: String,
    val userId: String
) {
    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(
                defaultJson
            )
        }
    }

    private fun HttpRequestBuilder.path(p: String) {
        this.addPath(serverAddress, p)
    }

    private fun HttpRequestBuilder.auth() {
        this.addAuthToken(platform, token)
    }

    suspend fun userInfo(): User {
        return client.get {
            auth()
            path("Users/$userId")
        }
    }

    suspend fun views(): JList<JView> {
        val json = client.get<String> {
            auth()
            path("Users/$userId/Views")
        }
        return defaultJson.parse(JList.serializer(JView.serializer()), json)
    }
}
