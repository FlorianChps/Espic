package com.fchps.data.di

import android.content.Context
import android.content.SharedPreferences
import com.fchps.data.api.FakeEsportApi
import com.fchps.data.datasource.local.FeedLocalDataSource
import com.fchps.data.datasource.local.LoginLocalDataSource
import com.fchps.data.datasource.local.impl.FeedLocalDataSourceImpl
import com.fchps.data.datasource.local.impl.LoginLocalDataSourceImpl
import com.fchps.data.datasource.remote.FeedRemoteDataSource
import com.fchps.data.datasource.remote.impl.FeedRemoteDataSourceImpl
import com.fchps.data.repositoryimpl.FeedRepositoryImpl
import com.fchps.data.repositoryimpl.LoginRepositoryImpl
import com.fchps.domain.repository.FeedRepository
import com.fchps.domain.repository.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    private const val BASE_URL = "https://mockbin.io/" // Replace with your API base URL

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(createJsonInstance().asConverterFactory(contentType)).client(
                OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }).build()
            ).build()
    }

    private fun createJsonInstance() = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): FakeEsportApi =
        retrofit.create(FakeEsportApi::class.java)

    @Provides
    fun provideRemoteDataSource(apiService: FakeEsportApi): FeedRemoteDataSource =
        FeedRemoteDataSourceImpl(apiService)

    @Provides
    fun provideFeedLocalDataSource(@ApplicationContext context: Context): FeedLocalDataSource =
        FeedLocalDataSourceImpl(context)

    @Provides
    fun provideLoginLocalDataSource(sharedPreferences: SharedPreferences): LoginLocalDataSource =
        LoginLocalDataSourceImpl(sharedPreferences)

    @Provides
    fun provideFeedRepository(
        remoteDataSource: FeedRemoteDataSource, localDataSource: FeedLocalDataSource
    ): FeedRepository = FeedRepositoryImpl(remoteDataSource, localDataSource)

    @Provides
    fun provideLoginRepository(localDataSource: LoginLocalDataSource): LoginRepository =
        LoginRepositoryImpl(localDataSource)

    // Provide SharedPreferences instance
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("espic_preferences", Context.MODE_PRIVATE)
}
