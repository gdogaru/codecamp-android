package com.gdogaru.codecamp.di

import android.app.Application
import android.content.Context
import com.gdogaru.codecamp.App
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 */
@Module
class ApplicationModule
@Inject constructor(var application: App) {

    @Singleton
    @Provides
    internal fun application(): Application = application

    @Singleton
    @Provides
    internal fun context(): Context = application

    @Singleton
    @Provides
    internal fun app(): App = application

}
