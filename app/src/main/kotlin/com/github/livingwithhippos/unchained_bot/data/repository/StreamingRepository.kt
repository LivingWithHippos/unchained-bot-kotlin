package com.github.livingwithhippos.unchained_bot.data.repository

import com.github.livingwithhippos.unchained_bot.data.model.Stream
import com.github.livingwithhippos.unchained_bot.data.remote.StreamingApiHelper

class StreamingRepository(private val streamingApiHelper: StreamingApiHelper) :
    BaseRepository() {

    suspend fun getStreams(token: String, id: String): Stream? {

        val streamResponse = safeApiCall(
            call = { streamingApiHelper.getStreams("Bearer $token", id) },
            errorMessage = "Error Fetching Streaming Info"
        )

        return streamResponse
    }
}
