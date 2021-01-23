package com.github.livingwithhippos.unchained_bot.data.remote

import com.github.livingwithhippos.unchained_bot.data.model.Stream
import retrofit2.Response

class StreamingApiHelperImpl(private val streamingApi: StreamingApi) :
    StreamingApiHelper {
    override suspend fun getStreams(token: String, id: String): Response<Stream> =
        streamingApi.getStreams(token, id)
}
