package com.github.livingwithhippos.unchained_bot.data.repository

import com.github.livingwithhippos.unchained_bot.data.model.User
import com.github.livingwithhippos.unchained_bot.data.remote.UserApiHelper

class UserRepository(private val userApiHelper: UserApiHelper) :
    BaseRepository() {

    suspend fun getUserInfo(token: String): User? {

        val userResponse = safeApiCall(
            call = { userApiHelper.getUser("Bearer $token") },
            errorMessage = "Error Fetching User Info"
        )

        return userResponse
    }
}
