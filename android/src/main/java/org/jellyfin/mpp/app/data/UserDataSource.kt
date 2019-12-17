package org.jellyfin.mpp.app.data

import android.util.Log
import io.ktor.client.features.ResponseException
import org.jellyfin.mpp.app.readAll
import org.jellyfin.mpp.common.JellyfinClient
import org.jellyfin.mpp.common.Platform
import org.jellyfin.mpp.common.authenticateUserByName
import java.util.*
import javax.inject.Inject

sealed class LoginError {
    object Credentials : LoginError()
    class Response(val response: String) : LoginError()
    class Other(val message: String, val exception: Throwable) : LoginError()

    override fun toString(): String {
        return when (this) {
            is Credentials -> "Invalid credentials"
            is Response -> "Server error:\n$response"
            is Other -> "Error:\n$message"
        }
    }
}

data class LoginResult(val id: String, val displayName: String, val token: String)

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class UserDataSource @Inject constructor() {
    suspend fun login(
        platform: Platform,
        url: String,
        username: String,
        password: String
    ): Result<LoginResult, LoginError> {
        return try {
            val resp = authenticateUserByName(platform, url, username, password)

            Result.Success(LoginResult(resp.User.Id, resp.User.Name, resp.AccessToken))
        } catch (e: ResponseException) {
            val response = e.response.readAll()

            if ("invalid user or password" in response.toLowerCase(Locale.ROOT)) {
                return Result.Error(LoginError.Credentials)
            }
            val log = "Got error from server:\n$response"
            Log.e(TAG, log)
            Result.Error(LoginError.Response(response))
        } catch (e: Throwable) {
            val msg = e.message
            if (msg != null) {
                Log.e(TAG, msg)
            }
            Result.Error(LoginError.Other("Error logging in", e))
        }
    }

    fun logout(client: JellyfinClient) {
        // TODO: revoke authentication
    }

    companion object {
        private const val TAG = "LoginDataSource"
    }
}
