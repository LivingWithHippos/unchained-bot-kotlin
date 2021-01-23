package com.github.livingwithhippos.unchained_bot.data.repository

import com.github.livingwithhippos.unchained_bot.data.remote.VariousApiHelper

class VariousApiRepository(private val variousApiHelper: VariousApiHelper) :
    BaseRepository() {

    suspend fun disableToken(token: String): Unit? {

        val response = safeApiCall(
            call = {
                variousApiHelper.disableToken(
                    token = "Bearer $token"
                )
            },
            errorMessage = "Error disabling token"
        )

        return response
    }
}
