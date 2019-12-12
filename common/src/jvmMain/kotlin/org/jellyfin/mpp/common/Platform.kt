package org.jellyfin.mpp.common

actual class Platform(
    androidVersion: String,
    actual val deviceName: String,
    actual val deviceId: String,
    actual val clientVersion: String
) {
    actual val client: String = "Android $androidVersion"
}
