package com.github.livingwithhippos.unchained_bot.data.repository

import com.github.livingwithhippos.unchained_bot.data.remote.DownloadApiHelper
import com.github.livingwithhippos.unchained_bot.data.model.DownloadItem

class DownloadRepository(private val downloadApiHelper: DownloadApiHelper) :
    BaseRepository() {
    suspend fun getDownloads(
        token: String,
        offset: Int? = 0,
        page: Int = 1,
        limit: Int = 50
    ): List<DownloadItem> {

        val downloadResponse = safeApiCall(
            call = { downloadApiHelper.getDownloads("Bearer $token", offset, page, limit) },
            errorMessage = "Error Fetching User Info"
        )

        return downloadResponse ?: emptyList()
    }

    suspend fun deleteDownload(token: String, id: String): Unit? {

        val response = safeApiCall(
            call = {
                downloadApiHelper.deleteDownload(
                    token = "Bearer $token",
                    id = id
                )
            },
            errorMessage = "Error deleting download"
        )

        return response
    }
}
