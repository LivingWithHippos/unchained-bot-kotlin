package com.github.livingwithhippos.unchained_bot.localization

object IT : Localization {
    override val helpMessage: String
        get() = """
        *Lista comandi:*
        /help - mostra la lista di comandi disponibili
        /user - mostra le informazioni utente
        /torrents \[numero, default 5] - mostra la lista degli ultimi n torrent
        /downloads \[numero, default 5] - mostra la lista degli ultimi n download
        /download \[url] - scarica un url nel server in cui sta girando il bot
        /unrestrict \[url|magnet|link file torrent] - genera un link di download. Magnet/Torrents verranno messi in coda, controlla il loro status con /torrents
        /transcode \[id file real debrid] - offre codifiche per streaming di varie qualità. Ottieni l'id con /unrestrict
    """.trimIndent()
}