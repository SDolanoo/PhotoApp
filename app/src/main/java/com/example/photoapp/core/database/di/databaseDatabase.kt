package com.example.photoapp.core.database.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.photoapp.features.odbiorca.data.OdbiorcaDao
import com.example.photoapp.features.sprzedawca.data.SprzedawcaDao
import com.example.photoapp.core.database.data.dao.UzytkownikDao
import com.example.photoapp.core.database.data.entities.Uzytkownik
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.FakturaDao
import com.example.photoapp.features.faktura.data.faktura.Produkt
import com.example.photoapp.features.produkt.data.ProduktDao
import com.example.photoapp.features.faktura.data.faktura.ProduktFaktura
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.produkt.data.ProduktFakturaDao
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Date
import javax.inject.Singleton

@Database(
    entities = [
        Uzytkownik::class, Odbiorca::class, Sprzedawca::class, Faktura::class, ProduktFaktura::class, Produkt::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun uzytkownikDao(): UzytkownikDao
    abstract fun sprzedawcaaDao(): SprzedawcaDao
    abstract fun odbiorcaDao(): OdbiorcaDao
    abstract fun fakturaDao(): FakturaDao
    abstract fun produktFakturaDao(): ProduktFakturaDao
    abstract fun produktDao(): ProduktDao

    // Dodaj inne DAO według potrzeby

}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {


    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context):
            AppDatabase {
        return Room.databaseBuilder(
            context = context,
            AppDatabase::class.java,
            "paragon_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideFakturaDao(appDatabase: AppDatabase): FakturaDao {
        return appDatabase.fakturaDao()
    }

    @Provides
    fun provideProduktFakturaDao(appDatabase: AppDatabase): ProduktFakturaDao {
        return appDatabase.produktFakturaDao()
    }

    @Provides
    fun provideProduktDao(appDatabase: AppDatabase): ProduktDao {
        return appDatabase.produktDao()
    }

    @Provides
    fun provideOdbiorcaDao(appDatabase: AppDatabase): OdbiorcaDao {
        return appDatabase.odbiorcaDao()
    }

    @Provides
    fun provideSprzedawcaDao(appDatabase: AppDatabase): SprzedawcaDao {
        return appDatabase.sprzedawcaaDao()
    }

    @Provides
    fun provideUzytkownikDao(appDatabase: AppDatabase): UzytkownikDao {
        return appDatabase.uzytkownikDao()
    }
}