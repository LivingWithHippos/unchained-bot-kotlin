package com.github.livingwithhippos.unchained_bot.data.remote

import com.github.livingwithhippos.unchained_bot.data.model.Host
import retrofit2.Response

class HostsApiHelperImpl(private val hostsApi: HostsApi) :
    HostsApiHelper {

    override suspend fun getHostsStatus(token: String): Response<Host> = hostsApi.getStreams(token)

    override suspend fun getHostsRegex(): Response<List<String>> = hostsApi.getHostsRegex()
}
