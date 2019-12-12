package org.jellyfin.mpp.app.ui.login

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import org.jellyfin.mpp.app.BuildConfig

import org.jellyfin.mpp.app.R
import org.jellyfin.mpp.common.JellyfinApi
import org.jellyfin.mpp.common.Platform
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.serverError != null) {
                url.error = getString(loginState.serverError)
            }
            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)

                setResult(Activity.RESULT_OK)

                //Complete and destroy login activity once successful
                finish()
            }
        })

        url.afterTextChanged {
            loginViewModel.loginDataChanged(
                url.text.toString(),
                username.text.toString(),
                password.text.toString()
            )
        }

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                url.text.toString(),
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    url.text.toString(),
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        val api = buildApi()
                        if (api != null) {
                            loginViewModel.login(
                                username.text.toString(),
                                password.text.toString(),
                                api
                            )
                        }
                    }
                }
                false
            }

            login.setOnClickListener {
                val api = buildApi()
                if (api != null) {
                    loading.visibility = View.VISIBLE
                    loginViewModel.login(username.text.toString(), password.text.toString(), api)
                }
            }
        }
    }

    private fun buildApi(): JellyfinApi? {
        val address = url.text.toString()
        val sp = getSharedPreferences("jellyfin", Context.MODE_PRIVATE)
        var uuidString = sp.getString("uuid", null)
        if (uuidString == null) {
            uuidString = UUID.randomUUID().toString()
            sp.edit().putString("uuid", uuidString).apply()
        }

        if (address != "") {
            return JellyfinApi(
                address,
                Platform(
                    Build.VERSION.RELEASE,
                    deviceName(),
                    uuidString,
                    BuildConfig.VERSION_NAME
                ),
                resources.displayMetrics.densityDpi
            )
        }
        return null
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private fun deviceName(): String {
            val m = Build.MANUFACTURER.toLowerCase(Locale.ROOT)
            val b = Build.BRAND.toLowerCase(Locale.ROOT)
            val d = Build.DEVICE.toLowerCase(Locale.ROOT)
            val p = Build.PRODUCT.toLowerCase(Locale.ROOT)
            return if (m in d || b in d) {
                Build.DEVICE
            } else if (m in p || b in p) {
                Build.PRODUCT
            } else {
                Build.MODEL
            }
        }
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
