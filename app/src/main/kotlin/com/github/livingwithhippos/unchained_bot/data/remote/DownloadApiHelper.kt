package com.github.livingwithhippos.unchained_bot.data.remote

import com.github.unchained_bot.unchained.data.model.DownloadItem
import retrofit2.Response

interface DownloadApiHelper {
    suspend fun getDownloads(
        token: String,
        offset: Int?,
        page: Int,
        limit: Int
    ): Response<List<DownloadItem>>

    suspend fun deleteDownload(
        token: String,
        id: String,
    ): Response<Unit>
}
