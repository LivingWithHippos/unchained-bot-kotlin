package com.github.livingwithhippos.unchained_bot.data.remote

import com.github.unchained_bot.unchained.data.model.DownloadItem
import retrofit2.Response

interface UnrestrictApiHelper {

    suspend fun getUnrestrictedLink(
        token: String,
        link: String,
        password: String? = null,
        remote: Int? = null
    ): Response<DownloadItem>
}
