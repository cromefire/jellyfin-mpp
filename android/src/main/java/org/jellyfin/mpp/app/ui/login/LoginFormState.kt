package org.jellyfin.mpp.app.ui.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val serverError: Int? = null,
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)
