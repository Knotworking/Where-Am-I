package com.knotworking.whereami.core.network.di

import com.knotworking.whereami.core.network.BuildConfig
import com.knotworking.whereami.core.network.FlickrService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideFlickrService(moshi: Moshi, okHttpClient: OkHttpClient): FlickrService {
        return Retrofit.Builder()
            .baseUrl("https://www.flickr.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FlickrService::class.java)
    }
    
    @Provides
    @Singleton
    @FlickrApiKey
    fun provideFlickrApiKey(): String = BuildConfig.FLICKR_API_KEY
}

@javax.inject.Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FlickrApiKey
