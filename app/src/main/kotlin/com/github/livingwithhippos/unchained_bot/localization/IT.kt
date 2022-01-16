package com.github.livingwithhippos.unchained_bot.localization

object IT : Localization {
    override val progress: String
        get() = "Progresso"
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
    override val privateKeyError: String
        get() = "Impossibile caricare i dati da Real Debrid.\nControlla la tua chiave API."
    override val botStarted: String
        get() = "Bot avviato"
    override val name: String
        get() = "Nome"
    override val size: String
        get() = "Dimensioni"
    override val link: String
        get() = "Link"
    override val transcodingInstructions: String
        get() = "Codifica streaming con "
    override val streamingUnavailable: String
        get() = "Streaming non disponibile"
    override val id: String
        get() = "id"
    override val username: String
        get() = "nome utente"
    override val email: String
        get() = "email"
    override val points: String
        get() = "punti"
    override val status: String
        get() = "stato"
    override val premium: String
        get() = "premium"
    override val days: String
        get() = "giorni"
    override val expiration: String
        get() = "scadenza"
    override val welcomeMessage: String
        get() = "Bentornato, %username%.\nHai %days% giorni di servizio premium\ne %points% punti rimanenti."
    override val unrestrict: String
        get() = "Sblocca"
    override val unrestrictDescription: String
        get() = "Sblocca un singolo link"
    override val startingDownload: String
        get() = "Avviando il download"
    override val getDownloadLink: String
        get() = "Ottieni link per download con"
    override val wrongDownloadSyntax: String
        get() = "Parametro errato o mancante.\nSintassi: /download [link sbloccato]"
    override val wrongStreamSyntax: String
        get() = "Parametro errato o mancante.\nSintassi: /stream [id file real debrid]"
    override val wrongUnrestrictSyntax: String
        get() = "Parametro errato o mancante.\nSintassi: /unrestrict [url|magnet|link file torrent]"
    override val addedTorrent: String
        get() = "Aggiunto torrent con id %id%, controlla il suo stato con /torrents"
    override val uploadingTorrent: String
        get() = "Caricando torrent su Real Debrid, controlla il suo stato con /torrents"
    override val appleQuality: String
        get() = "Qualità Apple"
    override val dashQuality: String
        get() = "Qualità Dash"
    override val liveMP4Quality: String
        get() = "Qualità liveMp4"
    override val h264WebMQuality: String
        get() = "Qualità h264WebM"
}
