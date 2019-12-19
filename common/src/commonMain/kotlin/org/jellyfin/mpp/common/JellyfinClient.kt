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

val clientJson = Json(
    JsonConfiguration.Stable.copy(
        strictMode = false,
        classDiscriminator = "Type"
    )
)

suspend fun authenticateUserByName(
    platform: Platform,
    address: String,
    name: String,
    password: String
): AuthenticateResponse {
    val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(clientJson)
        }
    }
    return client.post(body = AuthenticateRequest(name, password)) {
        addAuthToken(platform, null)
        contentType(ContentType.Application.Json)
        addPath(address, "Users/AuthenticateByName")
    }
}

class JellyfinClient(
    //val appStorage: Any,
    val serverAddress: String,
    val platform: Platform,
    val token: String,
    val userId: String
) {
    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(clientJson)
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

    suspend fun views(): JList<JView.CollectionFolder> {
        val json = client.get<String> {
            auth()
            path("Users/$userId/Views")
        }
        return clientJson.parse(JList.serializer(JView.CollectionFolder.serializer()), json)
    }

    suspend fun resume(
        mediaTypes: List<MediaType>,
        limit: Int = 12,
        recursive: Boolean = true,
        imageTypeLimit: Int = 1,
        enableImageTypes: List<ImageType> = listOf(ImageType.Primary),
        enableTotalRecordCount: Boolean = false
    ): JList<JView> {
        val json = client.get<String> {
            auth()
            path("Users/$userId/Items/Resume")
            parameter("Limit", limit)
            parameter("Recursive", recursive)
            parameter("Fields", "PrimaryImageAspectRatio,BasicSyncInfo")
            parameter("ImageTypeLimit", imageTypeLimit)
            parameter("EnableImageTypes", enableImageTypes.joinToString(","))
            parameter("EnableTotalRecordCount", enableTotalRecordCount)
            parameter("MediaTypes", mediaTypes.joinToString(","))
        }
        return clientJson.parse(JList.serializer(JView.serializer()), json)
    }
}
