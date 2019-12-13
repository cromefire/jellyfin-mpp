package org.jellyfin.mpp.app.data

import android.util.Log
import io.ktor.client.features.ResponseException
import kotlinx.coroutines.io.readUTF8Line
import org.jellyfin.mpp.app.data.model.LoggedInUser
import org.jellyfin.mpp.common.JellyfinApi
import java.util.*
import kotlin.collections.ArrayList

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

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    suspend fun login(
        username: String,
        password: String,
        api: JellyfinApi
    ): Result<LoggedInUser, LoginError> {
        return try {
            val resp = api.authenticateUserByName(username, password)

            Result.Success(LoggedInUser(resp.User.Id, resp.User.Id, resp.AccessToken))
        } catch (e: ResponseException) {
            val cnt = ArrayList<String>()
            while (e.response.content.availableForRead > 0) {
                val line = e.response.content.readUTF8Line()
                if (line != null) {
                    cnt.add(line)
                }
            }
            val response = cnt.joinToString("\n")

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

    fun logout(api: JellyfinApi) {
        // TODO: revoke authentication
    }

    companion object {
        private val TAG = "LoginDataSource"
    }
}
