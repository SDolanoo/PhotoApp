package com.example.photoapp.core.database.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.photoapp.database.data.dao.KategoriaDao
import com.example.photoapp.database.data.dao.OdbiorcaDao
import com.example.photoapp.database.data.dao.SprzedawcaDao
import com.example.photoapp.database.data.dao.UzytkownikDao
import com.example.photoapp.database.data.entities.Kategoria
import com.example.photoapp.database.data.entities.Odbiorca
import com.example.photoapp.database.data.entities.Sprzedawca
import com.example.photoapp.database.data.entities.Uzytkownik
import com.example.photoapp.ui.faktura.data.Faktura
import com.example.photoapp.ui.faktura.data.FakturaDao
import com.example.photoapp.ui.faktura.data.ProduktFaktura
import com.example.photoapp.ui.faktura.data.ProduktFakturaDao
import com.example.photoapp.ui.paragon.data.Paragon
import com.example.photoapp.ui.paragon.data.ParagonDao
import com.example.photoapp.ui.paragon.data.ProduktParagon
import com.example.photoapp.ui.paragon.data.ProduktParagonDao
import com.example.photoapp.ui.raportFiskalny.data.ProduktRaportFiskalny
import com.example.photoapp.ui.raportFiskalny.data.ProduktRaportFiskalnyDao
import com.example.photoapp.ui.raportFiskalny.data.RaportFiskalny
import com.example.photoapp.ui.raportFiskalny.data.RaportFiskalnyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Date
import javax.inject.Singleton

@Database(
    entities = [
        Uzytkownik::class, Odbiorca::class, Sprzedawca::class, Paragon::class,
        ProduktParagon::class, Faktura::class, ProduktFaktura::class, Kategoria::class,
        RaportFiskalny::class, ProduktRaportFiskalny::class
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
    abstract fun paragonDao(): ParagonDao
    abstract fun produktParagonDao(): ProduktParagonDao
    abstract fun kategoriaDao(): KategoriaDao
    abstract fun raportFiskalnyDao(): RaportFiskalnyDao
    abstract fun produktRaportFiskalnyDao(): ProduktRaportFiskalnyDao

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
            .build()
    }

    @Provides
    fun provideParagonDao(appDatabase: AppDatabase): ParagonDao {
        return appDatabase.paragonDao()
    }

    @Provides
    fun provideFakturaDao(appDatabase: AppDatabase): FakturaDao {
        return appDatabase.fakturaDao()
    }

    @Provides
    fun provideProduktParagonDao(appDatabase: AppDatabase): ProduktParagonDao {
        return appDatabase.produktParagonDao()
    }

    @Provides
    fun provideProduktFakturaDao(appDatabase: AppDatabase): ProduktFakturaDao {
        return appDatabase.produktFakturaDao()
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

    @Provides
    fun provideKategoriaDao(appDatabase: AppDatabase): KategoriaDao {
        return appDatabase.kategoriaDao()
    }

    @Provides
    fun provideRaportFiskalnyDao(appDatabase: AppDatabase): RaportFiskalnyDao {
        return appDatabase.raportFiskalnyDao()
    }

    @Provides
    fun provideProduktRaportFiskalnyDao(appDatabase: AppDatabase): ProduktRaportFiskalnyDao {
        return appDatabase.produktRaportFiskalnyDao()
    }

}