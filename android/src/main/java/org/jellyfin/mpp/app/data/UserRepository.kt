package org.jellyfin.mpp.app.data

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.serialization.SerializationException
import org.jellyfin.mpp.app.JellyfinApplication
import org.jellyfin.mpp.app.data.model.User
import org.jellyfin.mpp.app.platform
import org.jellyfin.mpp.app.securePrefs
import org.jellyfin.mpp.app.stableJson
import org.jellyfin.mpp.common.JellyfinClient
import java.util.*
import javax.inject.Inject

private var Context.user: User?
    get() {
        val userJson = securePrefs.getString("user", null) ?: return null
        return try {
            stableJson.parse(User.serializer(), userJson)
        } catch (e: SerializationException) {
            null
        }
    }
    set(value) {
        if (value == null) {
            securePrefs.edit().putString("user", null).apply()
            return
        }
        val str = stableJson.stringify(User.serializer(), value)
        securePrefs.edit().putString("user", str).apply()
    }

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
class UserRepository @Inject constructor(
    private val dataSource: UserDataSource,
    private val application: JellyfinApplication
) {

    // in-memory cache of the loggedInUser object
    var user: User? = application.user
        private set

    val isLoggedIn: Boolean
        get() = user != null

    fun logout(client: JellyfinClient) {
        user = null
        dataSource.logout(client)
        application.user = null
    }

    @Suppress("MoveVariableDeclarationIntoWhen")
    suspend fun login(
        username: String,
        password: String,
        url: String
    ): Result<User, LoginError> {
        val uuid = UUID.randomUUID().toString()
        val platform = application.platform(
            uuid = uuid
        )
        // handle login
        val result = dataSource.login(platform, url, username, password)

        return when (result) {
            is Result.Error -> result
            is Result.Success -> {
                val user = User(
                    result.data.id,
                    result.data.token,
                    result.data.displayName,
                    uuid,
                    url
                )

                setLoggedInUser(user)

                return Result.Success(user)
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun setLoggedInUser(user: User) {
        this.user = user
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        application.user = user
    }
}
