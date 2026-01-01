package com.example.audiobookreader.di

import android.content.Context
import com.example.audiobookreader.core.parser.ParserFactory
import com.example.audiobookreader.core.tts.TTSEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideTTSEngine(
        @ApplicationContext context: Context
    ): TTSEngine {
        return TTSEngine(context)
    }
    
    @Provides
    @Singleton
    fun provideParserFactory(): ParserFactory {
        return ParserFactory()
    }
}
