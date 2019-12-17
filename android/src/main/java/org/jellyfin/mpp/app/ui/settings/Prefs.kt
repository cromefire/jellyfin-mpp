package org.jellyfin.mpp.app.ui.settings

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

fun setNightMode(value: String?) {
    AppCompatDelegate.setDefaultNightMode(
        when (value) {
            "battery" -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "system" -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            } else {
                AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            }
        }
    )
}