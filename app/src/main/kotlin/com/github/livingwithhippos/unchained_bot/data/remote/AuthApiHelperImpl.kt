package com.github.livingwithhippos.unchained_bot.data.remote

import com.github.livingwithhippos.unchained_bot.data.model.Authentication
import com.github.livingwithhippos.unchained_bot.data.model.Secrets
import com.github.livingwithhippos.unchained_bot.data.model.Token
import retrofit2.Response

class AuthApiHelperImpl(private val authenticationApi: AuthenticationApi) :
    AuthApiHelper {

    override suspend fun getAuthentication(): Response<Authentication> =
        authenticationApi.getAuthentication()

    override suspend fun getSecrets(deviceCode: String): Response<Secrets> =
        authenticationApi.getSecrets(deviceCode = deviceCode)

    override suspend fun getToken(
        clientId: String,
        clientSecret: String,
        code: String
    ): Response<Token> = authenticationApi.getToken(clientId, clientSecret, code)
}
