package com.example.photoapp

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@HiltAndroidApp
class AppApplication: Application() {}

@Module
@InstallIn(SingletonComponent::class)
object DIAppModule {

    @Provides
    @Singleton
    fun  provideContext(application: Application): Context {
        return application.applicationContext
    }
}