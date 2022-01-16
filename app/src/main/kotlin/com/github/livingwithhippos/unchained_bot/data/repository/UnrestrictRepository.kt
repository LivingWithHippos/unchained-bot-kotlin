package com.github.livingwithhippos.unchained_bot.data.repository

import com.github.livingwithhippos.unchained_bot.data.remote.UnrestrictApiHelper
import com.github.livingwithhippos.unchained_bot.data.model.DownloadItem

class UnrestrictRepository(private val unrestrictApiHelper: UnrestrictApiHelper) :
    BaseRepository() {

    suspend fun getUnrestrictedLink(
        token: String,
        link: String,
        password: String? = null,
        remote: Int? = null
    ): DownloadItem? {

        return safeApiCall(
            call = {
                unrestrictApiHelper.getUnrestrictedLink(
                    token = "Bearer $token",
                    link = link,
                    password = password,
                    remote = remote
                )
            },
            errorMessage = "Error Fetching Unrestricted Link Info"
        )
    }
}
