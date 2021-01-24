package com.github.livingwithhippos.unchained_bot.data.repository

import com.github.livingwithhippos.unchained_bot.CREDENTIALS_PATH
import com.github.livingwithhippos.unchained_bot.PRIVATE_TOKEN
import com.github.livingwithhippos.unchained_bot.data.model.Credentials
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.io.BufferedReader
import java.io.File

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class CredentialsRepository(val moshi: Moshi) {

    fun getCredentials(): Credentials? {
        // read the file credentials.json with moshi
        val bufferedReader: BufferedReader = File(CREDENTIALS_PATH).bufferedReader()
        val credentials = bufferedReader.use { it.readText() }
        val adapter: JsonAdapter<Credentials> = moshi.adapter(Credentials::class.java)
        return adapter.fromJson(credentials)
    }

    fun insertCredentials(
        deviceCode: String,
        clientId: String?,
        clientSecret: String?,
        accessToken: String?,
        refreshToken: String?
    ) {
        val adapter: JsonAdapter<Credentials> = moshi.adapter(Credentials::class.java)
        val credentials = Credentials(deviceCode, clientId, clientSecret, accessToken, refreshToken)
        // val jsonWriter: JsonWriter = JsonWriter.of(Paths.get(CREDENTIALS_PATH).toFile().sink().buffer())
        val json = adapter.toJson(credentials)
        File(CREDENTIALS_PATH).writeText(json)
    }

    fun insertPrivateToken(accessToken: String) {
        insertCredentials(PRIVATE_TOKEN, PRIVATE_TOKEN, PRIVATE_TOKEN, accessToken, PRIVATE_TOKEN)
    }
}
