package org.jellyfin.mpp.app.data.model

import kotlinx.serialization.Serializable

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Serializable
data class User(
    val userId: String,
    val token: String,
    var displayName: String,
    val uuid: String,
    val address: String
)
