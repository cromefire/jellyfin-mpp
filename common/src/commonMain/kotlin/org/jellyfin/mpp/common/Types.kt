package org.jellyfin.mpp.common

import kotlinx.serialization.Serializable

@Serializable
data class User(val Id: String)

@Serializable
data class AuthenticateResponse(val ServerId: String, val AccessToken: String, val User: User)

@Serializable
data class AuthenticateRequest(val Pw: String, val Username: String)
