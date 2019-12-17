package org.jellyfin.mpp.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jellyfin.mpp.app.data.ApiService
import org.jellyfin.mpp.app.ui.login.LoginActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        GlobalScope.launch {
            apiService.update()

            val intent = if (apiService.loggedIn) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        if (apiService.isOffline) "Offline" else "Welcome back ${apiService.user.displayName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Intent(this@MainActivity, HomeActivity::class.java)
            } else {
                Intent(this@MainActivity, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}
