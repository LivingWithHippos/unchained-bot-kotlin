package com.github.livingwithhippos.unchained_bot.localization

interface Localization {
    val progress: String
    val helpMessage: String
    val privateKeyError: String
    val botStarted: String
    val name: String
    val size: String
    val link: String
    val transcodingInstructions: String
    val streamingUnavailable: String
    val id: String
    val username: String
    val email: String
    val points: String
    val status: String
    val premium: String
    val days: String
    val expiration: String
    val welcomeMessage: String
    val unrestrict: String
    val unrestrictDescription: String
    val unrestrictError: String
    val startingDownload: String
    val getDownloadLink: String
    val wrongDownloadSyntax: String
    val wrongStreamSyntax: String
    val wrongUnrestrictSyntax: String
    val addedTorrent: String
    val uploadingTorrent: String
    val appleQuality: String
    val dashQuality: String
    val liveMP4Quality: String
    val h264WebMQuality: String
    val error: String
}

val localeMapping: Map<String, Localization> = mapOf(
    Pair("en", EN),
    Pair("it", IT)
)