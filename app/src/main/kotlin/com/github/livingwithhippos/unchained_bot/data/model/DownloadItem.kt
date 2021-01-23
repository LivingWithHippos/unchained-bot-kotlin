package com.github.unchained_bot.unchained.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*
[
{
    "id": "string",
    "filename": "string",
    "mimeType": "string", // Mime Type of the file, guessed by the file extension
    "filesize": int, // bytes, 0 if unknown
    "link": "string", // Original link
    "host": "string", // Host main domain
    "chunks": int, // Max Chunks allowed
    "download": "string", // Generated link
    "generated": "string" // jsonDate
},
{
    "id": "string",
    "filename": "string",
    "mimeType": "string",
    "filesize": int,
    "link": "string",
    "host": "string",
    "chunks": int,
    "download": "string",
    "generated": "string",
    "type": "string" // Type of the file (in general, its quality)
}
]
*/

@JsonClass(generateAdapter = true)
data class DownloadItem(
    @Json(name = "id")
    val id: String,
    @Json(name = "filename")
    val filename: String,
    @Json(name = "mimeType")
    val mimeType: String?,
    @Json(name = "filesize")
    val fileSize: Long,
    @Json(name = "link")
    val link: String,
    @Json(name = "host")
    val host: String,
    @Json(name = "host_icon")
    val hostIcon: String?,
    @Json(name = "chunks")
    val chunks: Int,
    @Json(name = "crc")
    val crc: Int?,
    @Json(name = "download")
    val download: String,
    @Json(name = "streamable")
    val streamable: Int?,
    @Json(name = "generated")
    val generated: String?,
    @Json(name = "type")
    val type: String?,
    @Json(name = "alternative")
    val alternative: List<Alternative>?
)

@JsonClass(generateAdapter = true)
data class Alternative(
    @Json(name = "id")
    val id: String,
    @Json(name = "filename")
    val filename: String,
    @Json(name = "download")
    val download: String,
    @Json(name = "mimeType")
    val mimeType: String?,
    @Json(name = "quality")
    val quality: String?
)
