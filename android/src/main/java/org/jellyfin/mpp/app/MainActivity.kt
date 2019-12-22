package org.jellyfin.mpp.app

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import dagger.android.support.DaggerAppCompatActivity
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jellyfin.mpp.app.data.ApiService
import org.jellyfin.mpp.app.ui.crashlytics.CrashlyticsDialogFragment
import org.jellyfin.mpp.app.ui.crashlytics.CrashlyticsResult
import org.jellyfin.mpp.app.ui.login.LoginActivity
import javax.inject.Inject

class StopListener(private val cb: Animator.() -> Unit) : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator) {
        cb(animation)
    }

    override fun onAnimationEnd(animation: Animator) {
        cb(animation)
    }

    override fun onAnimationCancel(animation: Animator) {
        cb(animation)
    }

    override fun onAnimationStart(animation: Animator) {}
}

class MainActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        val listener = StopListener {
            logo.removeAllAnimatorListeners()

            val intent = if (apiService.loggedIn) {
                Toast.makeText(
                    this@MainActivity,
                    if (apiService.isOffline) "Offline" else "Welcome back ${apiService.user.displayName}",
                    Toast.LENGTH_SHORT
                ).show()
                Intent(this@MainActivity, HomeActivity::class.java)
            } else {
                Intent(this@MainActivity, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }

        suspend fun update() {
            apiService.update()
            runOnUiThread {
                logo.addAnimatorListener(listener)
            }
        }

        val sp = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
        val crlSet = sp.getBoolean("crashlytics-set", false)
        val crl = sp.getBoolean("crashlytics", false)
        if (!crlSet) {
            CrashlyticsDialogFragment().apply {
                show(supportFragmentManager, null)
                setListener {
                    when (it) {
                        CrashlyticsResult.ENABLE -> {
                            sp.edit()
                                .putBoolean("crashlytics", true)
                                .putBoolean("crashlytics-set", true)
                                .apply()
                            Fabric.with(this@MainActivity, Crashlytics())
                        }
                        CrashlyticsResult.DISABLE -> {
                            sp.edit()
                                .putBoolean("crashlytics", false)
                                .putBoolean("crashlytics-set", true)
                                .apply()
                        }
                        CrashlyticsResult.NEUTRAL -> {
                        }
                    }
                    GlobalScope.launch {
                        update()
                    }
                }
            }
        } else {
            if (crl) {
                Fabric.with(this@MainActivity, Crashlytics())
            }
            GlobalScope.launch {
                update()
            }
        }
    }
}
