package org.jellyfin.mpp.app.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val userId: String,
    val token: String,
    val displayName: String
)
