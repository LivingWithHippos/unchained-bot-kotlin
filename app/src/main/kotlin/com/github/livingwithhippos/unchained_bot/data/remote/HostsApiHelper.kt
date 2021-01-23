package com.github.livingwithhippos.unchained_bot.data.remote

import com.github.livingwithhippos.unchained_bot.data.model.Host
import retrofit2.Response

interface HostsApiHelper {
    suspend fun getHostsStatus(token: String): Response<Host>
    suspend fun getHostsRegex(): Response<List<String>>
}
