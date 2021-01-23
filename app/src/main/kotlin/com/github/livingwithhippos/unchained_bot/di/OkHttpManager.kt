package com.github.livingwithhippos.unchained_bot.di

import com.github.livingwithhippos.unchained_bot.data.model.EmptyBodyInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class OkHttpManager : OkHttpClient() {
    init {
        val logInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient().newBuilder()
            // logs all the calls
            .addInterceptor(logInterceptor)
            // avoid issues with empty bodies on delete/put and 20x return codes
            .addInterceptor(EmptyBodyInterceptor)
            .build()
    }
}
