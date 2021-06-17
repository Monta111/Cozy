package com.monta.cozy.di.module

import com.monta.cozy.BuildConfig
import com.monta.cozy.data.remote.api.EmailValidationApi
import com.monta.cozy.data.remote.api.PlaceApi
import com.monta.cozy.utils.consts.API_KEY_PARAM
import com.monta.cozy.utils.consts.CONNECT_TIMEOUT
import com.monta.cozy.utils.consts.EMAIL_VALIDATION_BASE_URL
import com.monta.cozy.utils.consts.PLACE_API_BASE_URL
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Named("MapAPI")
    @Singleton
    fun provideMapApiKeyInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            val url =
                request.url.newBuilder().addQueryParameter(API_KEY_PARAM, BuildConfig.MAPS_API_KEY)
                    .build()
            request = request.newBuilder().url(url).build()
            chain.proceed(request)
        }
    }

    @Provides
    @Named("EmailValidationAPI")
    @Singleton
    fun provideEmailVerifyApiKeyInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            request = request.newBuilder()
                .addHeader("x-rapidapi-key", "06ae4caab3mshbb7ac6a80bdfcecp1b9a55jsn18f1a7b48bed")
                .addHeader("x-rapidapi-host", "mailcheck.p.rapidapi.com")
                .build()
            chain.proceed(request)
        }
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
    fun provideHttpClientBuilder(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient.Builder {
        return OkHttpClient.Builder().apply {
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            if (BuildConfig.DEBUG) {
                addInterceptor(loggingInterceptor)
            }
        }
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Provides
    @Singleton
    fun providePlaceApi(
        okHttpBuilder: OkHttpClient.Builder,
        @Named("MapAPI") interceptor: Interceptor,
        retrofitBuilder: Retrofit.Builder
    ): PlaceApi {
        return retrofitBuilder
            .baseUrl(PLACE_API_BASE_URL)
            .client(okHttpBuilder.addInterceptor(interceptor).build())
            .build()
            .create(PlaceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideEmailValidationApi(
        okHttpBuilder: OkHttpClient.Builder,
        @Named("EmailValidationAPI") interceptor: Interceptor,
        retrofitBuilder: Retrofit.Builder
    ): EmailValidationApi {
        return retrofitBuilder
            .baseUrl(EMAIL_VALIDATION_BASE_URL)
            .client(okHttpBuilder.addInterceptor(interceptor).build())
            .build()
            .create(EmailValidationApi::class.java)
    }
}