package org.jellyfin.mpp.app.data

import android.util.Log
import io.ktor.client.features.ResponseException
import kotlinx.coroutines.io.readUTF8Line
import org.jellyfin.mpp.app.data.model.LoggedInUser
import org.jellyfin.mpp.common.JellyfinApi
import org.slf4j.Logger
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    suspend fun login(username: String, password: String, api: JellyfinApi): Result<LoggedInUser> {
        return try {
            val resp = api.authenticateUserByName(username, password)
            Result.Success(LoggedInUser(resp.User.Id, resp.User.Id))
        } catch (e: ResponseException) {
            var cnt = "Got error from server:"
            while (e.response.content.availableForRead > 0) {
                cnt += "\n" + e.response.content.readUTF8Line()
            }
            Log.e(TAG, cnt)
            Result.Error(IOException("Got error from server", e))
        } catch (e: Throwable) {
            val msg = e.message;
            if (msg != null) {
                Log.e(TAG, msg)
            }
            Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout(api: JellyfinApi) {
        // TODO: revoke authentication
    }

    companion object {
        private val TAG = "LoginDataSource";
    }
}
