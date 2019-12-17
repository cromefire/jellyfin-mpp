package org.jellyfin.mpp.app.dagger

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import org.jellyfin.mpp.app.HomeActivity
import org.jellyfin.mpp.app.JellyfinApplication
import org.jellyfin.mpp.app.MainActivity
import org.jellyfin.mpp.app.ui.home.HomeFragment
import org.jellyfin.mpp.app.ui.login.LoginActivity
import javax.inject.Singleton


@Module(includes = [AndroidSupportInjectionModule::class])
abstract class AppModule {
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
    @ContributesAndroidInjector
    abstract fun contributeLoginActivity(): LoginActivity
    @ContributesAndroidInjector
    abstract fun contributeHomeActivity(): HomeActivity
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @Singleton
    @Binds
    @AppContext
    abstract fun provideContext(app: JellyfinApplication): Context
}
