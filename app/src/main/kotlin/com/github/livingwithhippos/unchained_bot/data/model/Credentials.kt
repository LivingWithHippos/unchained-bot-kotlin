package com.github.livingwithhippos.unchained_bot.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The credentials db entity.
 * It can store either a token obtained with the oauth process or a private api token which will populate all fields but accessToken with PRIVATE_TOKEN
 */

@JsonClass(generateAdapter = true)
data class Credentials(
    @Json(name = "device_code")
    val deviceCode: String,
    @Json(name = "client_id")
    val clientId: String?,
    @Json(name = "client_secret")
    val clientSecret: String?,
    @Json(name = "access_token")
    val accessToken: String?,
    @Json(name = "refresh_token")
    val refreshToken: String?
)
