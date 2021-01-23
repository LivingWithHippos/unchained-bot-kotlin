package com.github.livingwithhippos.unchained_bot.data.remote

import com.github.livingwithhippos.unchained_bot.data.model.User
import retrofit2.Response

class UserApiHelperImpl(private val userApi: UserApi) :
    UserApiHelper {
    override suspend fun getUser(token: String): Response<User> = userApi.getUser(token)
}
