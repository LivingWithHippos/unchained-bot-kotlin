package com.github.livingwithhippos.unchained_bot.data.remote

import com.github.unchained_bot.unchained.data.model.DownloadItem
import retrofit2.Response

class UnrestrictApiHelperImpl(private val unrestrictApi: UnrestrictApi) :
    UnrestrictApiHelper {
    override suspend fun getUnrestrictedLink(
        token: String,
        link: String,
        password: String?,
        remote: Int?
    ): Response<DownloadItem> =
        unrestrictApi.getUnrestrictedLink(token, link, password, remote)
}
