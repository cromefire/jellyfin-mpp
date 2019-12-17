package org.jellyfin.mpp.common

expect class Platform {
    val client: String
    val clientVersion: String
    val deviceName: String
    val deviceId: String
    val devicePixelRatio: Int
}
