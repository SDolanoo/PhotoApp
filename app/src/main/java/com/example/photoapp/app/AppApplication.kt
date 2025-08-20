package com.example.photoapp.app

import android.app.Application
import android.content.Context
import com.example.photoapp.features.faktura.data.faktura.FakturaService
import com.example.photoapp.features.odbiorca.data.OdbiorcaService
import com.example.photoapp.features.produkt.data.ProduktFakturaService
import com.example.photoapp.features.produkt.data.ProduktService
import com.example.photoapp.features.sprzedawca.data.SprzedawcaService
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

    @Provides
    fun provideFakturaDao(): FakturaService = FakturaService()

    @Provides
    fun provideProduktFakturaDao(): ProduktFakturaService = ProduktFakturaService()

    @Provides
    fun provideProduktDao(): ProduktService = ProduktService()

    @Provides
    fun provideOdbiorcaDao(): OdbiorcaService = OdbiorcaService()

    @Provides
    fun provideSprzedawcaDao(): SprzedawcaService = SprzedawcaService()
}