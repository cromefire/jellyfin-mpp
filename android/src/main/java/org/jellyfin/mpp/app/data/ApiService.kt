package org.jellyfin.mpp.app.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.ktor.client.features.ClientRequestException
import org.jellyfin.mpp.app.JellyfinApplication
import org.jellyfin.mpp.app.data.enums.ApiState
import org.jellyfin.mpp.app.data.model.User
import org.jellyfin.mpp.app.platform
import org.jellyfin.mpp.app.readAll
import org.jellyfin.mpp.common.JellyfinClient
import javax.inject.Inject


class ApiService @Inject constructor(
    private val application: JellyfinApplication,
    private val userRepository: UserRepository
) {
    val state = MutableLiveData<ApiState>(ApiState.OFFLINE)

    private val User.client: JellyfinClient
        get() {
            return JellyfinClient(
                serverAddress = address,
                platform = application.platform(uuid),
                token = token,
                userId = userId
            )
        }

    val user: User
        get() {
            return userRepository.user ?: throw RuntimeException("User unavailable")
        }

    val client: JellyfinClient
        get() {
            val user = userRepository.user ?: throw RuntimeException("Api unavailable")
            return user.client
        }

    val isOffline: Boolean
        get() = state.value == ApiState.OFFLINE

    val loggedIn: Boolean
        get() = userRepository.isLoggedIn

    suspend fun update(): ApiState {
        fun setState(s: ApiState): ApiState {
            state.postValue(s)
            return s
        }

        if (loggedIn) {
            val apiUser = try {
                client.userInfo()
            } catch (e: ClientRequestException) {
                val response = e.response.readAll()

                Log.w(TAG, "Error updating user info: $response (${e.response.status})")
                return setState(ApiState.OFFLINE)
            }
            user.displayName = apiUser.Name
            return setState(ApiState.ONLINE)
        } else {
            return setState(ApiState.OFFLINE)
        }
    }

    companion object {
        private val TAG = "ApiService"
    }
}
