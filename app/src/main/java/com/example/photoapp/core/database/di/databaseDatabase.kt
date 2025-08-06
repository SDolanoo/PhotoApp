package com.example.photoapp.core.database.di

//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.room.TypeConverter
//import androidx.room.TypeConverters
//import com.example.photoapp.core.database.data.dao.UzytkownikService
//import com.example.photoapp.core.database.data.entities.Uzytkownik
//import com.example.photoapp.features.faktura.data.faktura.Faktura
//import com.example.photoapp.features.faktura.data.faktura.FakturaService
//import com.example.photoapp.features.odbiorca.data.Odbiorca
//import com.example.photoapp.features.odbiorca.data.OdbiorcaService
//import com.example.photoapp.features.produkt.data.Produkt
//import com.example.photoapp.features.produkt.data.ProduktFaktura
//import com.example.photoapp.features.produkt.data.ProduktFakturaService
//import com.example.photoapp.features.produkt.data.ProduktService
//import com.example.photoapp.features.sprzedawca.data.Sprzedawca
//import com.example.photoapp.features.sprzedawca.data.SprzedawcaService
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import java.util.Date
//import javax.inject.Singleton
//
//@Database(
//    entities = [
//        Uzytkownik::class, Odbiorca::class, Sprzedawca::class, Faktura::class, ProduktFaktura::class, Produkt::class
//    ],
//    version = 1
//)
//@TypeConverters(Converters::class)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun uzytkownikService(): UzytkownikService
//    abstract fun sprzedawcaaService(): SprzedawcaService
//    abstract fun odbiorcaService(): OdbiorcaService
//    abstract fun fakturaService(): FakturaService
//    abstract fun produktFakturaService(): ProduktFakturaService
//    abstract fun produktService(): ProduktService
//
//    // Dodaj inne DAO według potrzeby
//
//}
//
//class Converters {
//    @TypeConverter
//    fun fromTimestamp(value: Long?): Date? {
//        return value?.let { Date(it) }
//    }
//
//    @TypeConverter
//    fun dateToTimestamp(date: Date?): Long? {
//        return date?.time?.toLong()
//    }
//}
//
//@InstallIn(SingletonComponent::class)
//@Module
//object DatabaseModule {
//
//
//    @Provides
//    @Singleton
//    fun provideAppDatabase(@ApplicationContext context: Context):
//            AppDatabase {
//        return Room.databaseBuilder(
//            context = context,
//            AppDatabase::class.java,
//            "paragon_database"
//        )
//            .fallbackToDestructiveMigration()
//            .build()
//    }
//
//    @Provides
//    fun provideFakturaDao(appDatabase: AppDatabase): FakturaService {
//        return appDatabase.fakturaService()
//    }
//
//    @Provides
//    fun provideProduktFakturaDao(appDatabase: AppDatabase): ProduktFakturaService {
//        return appDatabase.produktFakturaService()
//    }
//
//    @Provides
//    fun provideProduktDao(appDatabase: AppDatabase): ProduktService {
//        return appDatabase.produktService()
//    }
//
//    @Provides
//    fun provideOdbiorcaDao(appDatabase: AppDatabase): OdbiorcaService {
//        return appDatabase.odbiorcaService()
//    }
//
//    @Provides
//    fun provideSprzedawcaDao(appDatabase: AppDatabase): SprzedawcaService {
//        return appDatabase.sprzedawcaaService()
//    }
//
//    @Provides
//    fun provideUzytkownikDao(appDatabase: AppDatabase): UzytkownikService {
//        return appDatabase.uzytkownikService()
//    }
//}