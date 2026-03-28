package com.knotworking.whereami.data.photo.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.knotworking.whereami.data.photo.BuildConfig
import com.knotworking.whereami.data.photo.datasource.api.BenHikesApi
import com.knotworking.whereami.data.photo.datasource.api.FlickrApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PhotoDataModule {

    @Provides
    @Singleton
    fun provideFlickrService(moshi: Moshi, okHttpClient: OkHttpClient): FlickrApi {
        return Retrofit.Builder()
            .baseUrl("https://www.flickr.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FlickrApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBenHikesService(moshi: Moshi, okHttpClient: OkHttpClient): BenHikesApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BENHIKES_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(BenHikesApi::class.java)
    }

    @Provides
    @Singleton
    @FlickrApiKey
    fun provideFlickrApiKey(): String = BuildConfig.FLICKR_API_KEY

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("settings") }
        )
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FlickrApiKey
