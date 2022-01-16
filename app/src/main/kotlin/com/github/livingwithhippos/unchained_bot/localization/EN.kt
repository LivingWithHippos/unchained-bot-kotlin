package com.github.livingwithhippos.unchained_bot.localization

object EN : Localization {
    override val progress: String
        get() = "Progress"
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
    override val privateKeyError: String
        get() = "Couldn't load your real debrid data\nCheck your private api key."
    override val botStarted: String
        get() = "Bot started"
    override val name: String
        get() = "Name"
    override val size: String
        get() = "Size"
    override val link: String
        get() = "Link"
    override val transcodingInstructions: String
        get() = "Streaming transcoding with"
    override val streamingUnavailable: String
        get() = "Streaming not available"
    override val id: String
        get() = "id"
    override val username: String
        get() = "username"
    override val email: String
        get() =  "email"
    override val points: String
        get() = "points"
    override val status: String
        get() = "status"
    override val premium: String
        get() = "premium"
    override val days: String
        get() = "days"
    override val expiration: String
        get() = "expiration"
    override val welcomeMessage: String
        get() = "Welcome back, %username%.\nYou have %days% days of premium\nand %points% points remaining."
    override val unrestrict: String
        get() =  "Unrestrict"
    override val unrestrictDescription: String
        get() = "Unrestrict a single link"
    override val startingDownload: String
        get() = "Starting download"
    override val getDownloadLink: String
        get() = "Get the download link with"
}