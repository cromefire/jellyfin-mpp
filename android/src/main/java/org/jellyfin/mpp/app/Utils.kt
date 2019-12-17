package org.jellyfin.mpp.app

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import io.ktor.client.response.HttpResponse
import kotlinx.coroutines.io.readUTF8Line
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.jellyfin.mpp.common.Platform
import java.util.*


data class Pointer<T>(var value: T?)

val Context.securePrefs: SharedPreferences
    get() {
        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
            "jellyfin-secret",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

val stableJson = Json(JsonConfiguration.Stable)

fun deviceName(): String {
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

fun Context.platform(uuid: String): Platform {
    return Platform(
        Build.VERSION.RELEASE,
        deviceName(),
        uuid,
        BuildConfig.VERSION_NAME,
        resources.displayMetrics.densityDpi
    )
}

suspend fun HttpResponse.readAll(): String {
    val cnt = ArrayList<String>()
    while (content.availableForRead > 0) {
        val line = content.readUTF8Line()
        if (line != null) {
            cnt.add(line)
        }
    }
    return cnt.joinToString("\n")
}
