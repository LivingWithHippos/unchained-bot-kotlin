package com.github.livingwithhippos.unchained_bot.data.remote

import com.github.livingwithhippos.unchained_bot.data.model.User
import retrofit2.Response

interface UserApiHelper {

    suspend fun getUser(token: String): Response<User>
}
