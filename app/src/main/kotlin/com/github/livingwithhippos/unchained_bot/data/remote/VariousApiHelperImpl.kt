package com.github.livingwithhippos.unchained_bot.data.remote

import retrofit2.Response

class VariousApiHelperImpl(private val variousApi: VariousApi) :
    VariousApiHelper {
    override suspend fun disableToken(token: String): Response<Unit> =
        variousApi.disableToken(token)
}
