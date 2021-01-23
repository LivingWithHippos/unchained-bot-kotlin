package com.github.livingwithhippos.unchained_bot.di

import com.github.livingwithhippos.unchained_bot.BASE_AUTH_URL
import com.github.livingwithhippos.unchained_bot.BASE_URL
import com.github.livingwithhippos.unchained_bot.data.model.EmptyBodyInterceptor
import com.github.livingwithhippos.unchained_bot.data.remote.DownloadsApi
import com.github.livingwithhippos.unchained_bot.data.remote.StreamingApi
import com.github.livingwithhippos.unchained_bot.data.remote.TorrentsApi
import com.github.livingwithhippos.unchained_bot.data.remote.UnrestrictApi
import com.github.livingwithhippos.unchained_bot.data.remote.UserApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * This object manages the Dagger-Hilt injection for the  OkHttp and Retrofit clients
 */

object ApiFactory {

    fun provideOkHttpClient(): OkHttpClient {
        // remove in production
        /*
        val logInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
         */

        return OkHttpClient().newBuilder()
            // logs all the calls, removed in the release channel
            // .addInterceptor(logInterceptor)
            // avoid issues with empty bodies on delete/put and 20x return codes
            .addInterceptor(EmptyBodyInterceptor)
            .build()
    }

    fun authRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BASE_AUTH_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    fun apiRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    fun provideUnrestrictApi(retrofit: Retrofit): UnrestrictApi {
        return retrofit.create(UnrestrictApi::class.java)
    }

    fun provideStreamingApi(retrofit: Retrofit): StreamingApi {
        return retrofit.create(StreamingApi::class.java)
    }

    fun provideTorrentsApi(retrofit: Retrofit): TorrentsApi {
        return retrofit.create(TorrentsApi::class.java)
    }

    fun provideDownloadsApi(retrofit: Retrofit): DownloadsApi {
        return retrofit.create(DownloadsApi::class.java)
    }
}
