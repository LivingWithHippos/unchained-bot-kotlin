package com.github.livingwithhippos.unchained_bot.data.local

object UnchaineDB {
    // move to environment variable?
    private const val dbPath = "/home/user/IdeaProjects/unchained-bot-kotlin/src/main/kotlin/com/github/livingwithhippos/unchained_bot/config/credentials.json"

    private const val credentialsPath = "./config/credentials.json"

    // controlla i parametri di
}

object CredentialsManager {
    const val credentialsPath = "./config/credentials.json"

    fun savePrivateToken(privateAccessToken: String?) {

        if (privateAccessToken != null) {
            // scrivi su json il valore
        }
    }
}
