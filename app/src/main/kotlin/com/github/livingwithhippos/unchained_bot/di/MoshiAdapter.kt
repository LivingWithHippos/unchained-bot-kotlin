package com.github.livingwithhippos.unchained_bot.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MoshiAdapter {

    fun getBuilder(): Moshi {
        // KotlinJsonAdapterFactory()
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
}
