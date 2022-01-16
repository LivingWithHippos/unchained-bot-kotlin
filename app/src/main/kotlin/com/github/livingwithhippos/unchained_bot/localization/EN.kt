package com.github.livingwithhippos.unchained_bot.localization

object EN : Localization {
    override val helpMessage: String
        get() = """
        *Command list:*
        /help - display the list of available commands
        /user - get Real Debrid user's information
        /torrents [number, default 5] - list the last torrents
        /downloads [number, default 5] - list the last downloads
        /download [unrestricted link] - downloads the link on the directory of the server running the bot
        /unrestrict [url|magnet|torrent file link] - generate a download link. Magnet/Torrents will be queued, check their status with /torrents
        /transcode [real debrid file id] - transcode streaming links to various quality levels. Get the file id using /unrestrict
    """.trimIndent()
}