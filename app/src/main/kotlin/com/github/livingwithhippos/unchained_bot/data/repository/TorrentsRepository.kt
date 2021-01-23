package com.github.livingwithhippos.unchained_bot.data.repository

import com.github.livingwithhippos.unchained_bot.data.model.AvailableHost
import com.github.livingwithhippos.unchained_bot.data.model.TorrentItem
import com.github.livingwithhippos.unchained_bot.data.model.UploadedTorrent
import com.github.livingwithhippos.unchained_bot.data.remote.TorrentApiHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class TorrentsRepository(private val torrentApiHelper: TorrentApiHelper) :
    BaseRepository() {

    suspend fun getAvailableHosts(token: String): List<AvailableHost>? {
        val hostResponse: List<AvailableHost>? = safeApiCall(
            call = { torrentApiHelper.getAvailableHosts(token = "Bearer $token") },
            errorMessage = "Error Retrieving Available Hosts"
        )

        return hostResponse
    }

    suspend fun getTorrentInfo(
        token: String,
        id: String
    ): TorrentItem? {
        val torrentResponse: TorrentItem? = safeApiCall(
            call = {
                torrentApiHelper.getTorrentInfo(
                    token = "Bearer $token",
                    id = id
                )
            },
            errorMessage = "Error Retrieving Torrent Info"
        )

        return torrentResponse
    }

    suspend fun addTorrent(
        token: String,
        binaryTorrent: ByteArray,
        host: String
    ): UploadedTorrent? {

        val requestBody: RequestBody = binaryTorrent.toRequestBody(
            "application/octet-stream".toMediaTypeOrNull(),
            0,
            binaryTorrent.size
        )

        val addTorrentResponse = safeApiCall(
            call = {
                torrentApiHelper.addTorrent(
                    token = "Bearer $token",
                    binaryTorrent = requestBody,
                    host = host
                )
            },
            errorMessage = "Error Uploading Torrent"
        )

        return addTorrentResponse
    }

    suspend fun addMagnet(
        token: String,
        magnet: String,
        host: String
    ): UploadedTorrent? {
        val torrentResponse = safeApiCall(
            call = {
                torrentApiHelper.addMagnet(
                    token = "Bearer $token",
                    magnet = magnet,
                    host = host
                )
            },
            errorMessage = "Error Uploading Torrent From Magnet"
        )

        return torrentResponse
    }

    suspend fun getTorrentsList(
        token: String,
        offset: Int? = null,
        page: Int? = 1,
        limit: Int? = 50,
        filter: String? = null
    ): List<TorrentItem> {

        val torrentsResponse: List<TorrentItem>? = safeApiCall(
            call = {
                torrentApiHelper.getTorrentsList(
                    token = "Bearer $token",
                    offset = offset,
                    page = page,
                    limit = limit,
                    filter = filter
                )
            },
            errorMessage = "Error Retrieving Torrent Info"
        )

        return torrentsResponse ?: emptyList()
    }

    suspend fun selectFiles(
        token: String,
        id: String,
        files: String = "all"
    ) {

        // this call has no return type
        safeApiCall(
            call = {
                torrentApiHelper.selectFiles(
                    token = "Bearer $token",
                    id = id,
                    files = files
                )
            },
            errorMessage = "Error Selecting Torrent Files"
        )
    }
}
