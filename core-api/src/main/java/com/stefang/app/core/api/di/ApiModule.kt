package com.stefang.app.core.api.di

import com.stefang.app.core.api.BuildConfig
import com.stefang.app.core.api.CurrencyRemoteDataSource
import com.stefang.app.core.api.OpenExchangeDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ApiModule {

    @Binds
    fun provideCurrencyRemoteDataSource(dataSource: OpenExchangeDataSource): CurrencyRemoteDataSource

    companion object {

        private const val BASE_URL = "https://openexchangerates.org/api/"

        @Provides
        @Singleton
        fun okHttpCallFactory(): Call.Factory = OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                    },
            )
            .build()

        @Provides
        @Singleton
        fun provideRetrofit(okhttpCallFactory: Call.Factory) = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .callFactory(okhttpCallFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
