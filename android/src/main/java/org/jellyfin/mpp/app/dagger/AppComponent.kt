package org.jellyfin.mpp.app.dagger

import dagger.Component
import dagger.android.AndroidInjector
import org.jellyfin.mpp.app.JellyfinApplication
import javax.inject.Singleton

// Definition of the Application graph
@Singleton
@Component(
    modules = [AppModule::class]
)
interface AppComponent : AndroidInjector<JellyfinApplication> {
    @Component.Factory
    abstract class Builder : AndroidInjector.Factory<JellyfinApplication>
}
