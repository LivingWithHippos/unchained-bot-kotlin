package com.github.livingwithhippos.unchained_bot.data.remote

import com.github.livingwithhippos.unchained_bot.data.model.Stream
import retrofit2.Response

interface StreamingApiHelper {
    suspend fun getStreams(token: String, id: String): Response<Stream>
}
