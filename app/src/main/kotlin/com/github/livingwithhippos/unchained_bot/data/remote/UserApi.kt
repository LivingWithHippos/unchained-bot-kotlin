package com.github.livingwithhippos.unchained_bot.data.remote

import com.github.livingwithhippos.unchained_bot.data.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApi {
    @GET("user")
    suspend fun getUser(@Header("Authorization") token: String): Response<User>
}
