package com.example.audiobookreader.di

import android.content.Context
import androidx.room.Room
import com.example.audiobookreader.data.local.AudioBookDatabase
import com.example.audiobookreader.data.local.dao.BookDao
import com.example.audiobookreader.data.repository.BookRepositoryImpl
import com.example.audiobookreader.domain.repository.BookRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAudioBookDatabase(
        @ApplicationContext context: Context
    ): AudioBookDatabase {
        return Room.databaseBuilder(
            context,
            AudioBookDatabase::class.java,
            AudioBookDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideBookDao(database: AudioBookDatabase): BookDao {
        return database.bookDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindBookRepository(
        bookRepositoryImpl: BookRepositoryImpl
    ): BookRepository
}
