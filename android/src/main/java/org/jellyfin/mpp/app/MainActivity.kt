package org.jellyfin.mpp.app

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jellyfin.mpp.app.data.ApiService
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
    @Inject lateinit var apiService: ApiService

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

        GlobalScope.launch {
            apiService.update()
            runOnUiThread {
                logo.addAnimatorListener(listener)
            }
        }
    }
}
