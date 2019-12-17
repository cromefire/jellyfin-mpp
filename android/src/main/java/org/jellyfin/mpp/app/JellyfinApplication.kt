package org.jellyfin.mpp.app

import com.facebook.drawee.backends.pipeline.Fresco
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import org.jellyfin.mpp.app.dagger.DaggerAppComponent


// appComponent lives in the Application class to share its lifecycle
class JellyfinApplication : DaggerApplication() {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }
}
