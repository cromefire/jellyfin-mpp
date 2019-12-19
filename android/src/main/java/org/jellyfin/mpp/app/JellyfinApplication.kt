package org.jellyfin.mpp.app

import androidx.preference.PreferenceManager
import com.facebook.drawee.backends.pipeline.Fresco
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import org.jellyfin.mpp.app.dagger.DaggerAppComponent
import org.jellyfin.mpp.app.ui.settings.setNightMode


// appComponent lives in the Application class to share its lifecycle
class JellyfinApplication : DaggerApplication() {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        setNightMode(sp.getString("theme", null))
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }
}
