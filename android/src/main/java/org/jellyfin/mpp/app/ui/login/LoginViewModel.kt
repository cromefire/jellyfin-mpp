package org.jellyfin.mpp.app.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import org.jellyfin.mpp.app.data.LoginRepository
import org.jellyfin.mpp.app.data.Result
import kotlinx.coroutines.*

import org.jellyfin.mpp.app.R
import org.jellyfin.mpp.common.JellyfinApi

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String, api: JellyfinApi) {
        // launched in a separate asynchronous job
        GlobalScope.launch {
            val result = loginRepository.login(username, password, api)

            _loginResult.postValue(
                if (result is Result.Success) {
                    LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
                } else {
                    LoginResult(error = R.string.login_failed)
                }
            )
        }
    }

    fun loginDataChanged(url: String, username: String, password: String) {
        if (!isUrlValid(url)) {
            _loginForm.value = LoginFormState(serverError = R.string.invalid_server)
        } else if (username.isBlank()) {
            _loginForm.value = LoginFormState(usernameError = R.string.username_required)
        } else if (password.isBlank()) {
            _loginForm.value = LoginFormState(passwordError = R.string.password_required)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isUrlValid(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }
}
