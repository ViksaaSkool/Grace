package com.grace.app.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.grace.app.custom.FoodDetector
import com.grace.app.data.MealRepository
import com.grace.app.data.MealRepositoryImpl
import com.grace.app.preferences.AppPreferences
import com.grace.app.preferences.SharedPreferencesAppPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideFoodDetector(): FoodDetector = FoodDetector()

    @Provides
    @Singleton
    fun provideMealRepository(impl: MealRepositoryImpl): MealRepository = impl

    @Provides
    @Singleton
    fun provideAppPreferences(preferences: SharedPreferences): AppPreferences {
        return SharedPreferencesAppPreferences(preferences)
    }

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
