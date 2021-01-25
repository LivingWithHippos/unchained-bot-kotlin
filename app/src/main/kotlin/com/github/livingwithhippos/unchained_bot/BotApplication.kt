package com.github.livingwithhippos.unchained_bot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.network.fold
import com.github.livingwithhippos.unchained_bot.data.model.Stream
import com.github.livingwithhippos.unchained_bot.data.model.TorrentItem
import com.github.livingwithhippos.unchained_bot.data.model.UploadedTorrent
import com.github.livingwithhippos.unchained_bot.data.model.User
import com.github.livingwithhippos.unchained_bot.data.repository.DownloadRepository
import com.github.livingwithhippos.unchained_bot.data.repository.StreamingRepository
import com.github.livingwithhippos.unchained_bot.data.repository.TorrentsRepository
import com.github.livingwithhippos.unchained_bot.data.repository.UnrestrictRepository
import com.github.livingwithhippos.unchained_bot.data.repository.UserRepository
import com.github.livingwithhippos.unchained_bot.utilities.isMagnet
import com.github.livingwithhippos.unchained_bot.utilities.isTorrent
import com.github.livingwithhippos.unchained_bot.utilities.isWebUrl
import com.github.livingwithhippos.unchained_bot.utilities.runCommand
import com.github.unchained_bot.unchained.data.model.DownloadItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import kotlin.system.exitProcess

@KoinApiExtension
class BotApplication : KoinComponent {

    // Environment variables
    private val botToken: String = getKoin().getProperty("TELEGRAM_BOT_TOKEN") ?: ""
    private val privateApiKey: String = getKoin().getProperty("PRIVATE_API_KEY") ?: ""
    private val wgetArguments: String = getKoin().getProperty("WGET_ARGUMENTS") ?: "--no-verbose"
    private val logLevelArgument: String = getKoin().getProperty("LOG_LEVEL") ?: "error"

    // these are not useful for docker but for running it locally
    private val tempPath: String = getKoin().getProperty("TEMP_PATH") ?: "/tmp/"
    private val downloadsPath: String = getKoin().getProperty("DOWNLOADS_PATH") ?: "/downloads/"

    // repositories
    private val userRepository: UserRepository by inject()
    private val unrestrictRepository: UnrestrictRepository by inject()
    private val streamingRepository: StreamingRepository by inject()
    private val torrentsRepository: TorrentsRepository by inject()
    private val downloadRepository: DownloadRepository by inject()

    private val okHttpClient: OkHttpClient by inject()

    // coroutines
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val helpMessage = """
        *Command list:*
        /help - display the list of available commands
        /user - get Real Debrid user's information
        /torrents [number, default 5] - list the last torrents
        /downloads [number, default 5] - list the last downloads
        /download [unrestricted link] - downloads the link on the directory of the server running the bot
        /unrestrict [url|magnet|torrent file link] - generate a download link. Magnet/Torrents will be queued, check their status with /torrents
        /transcode [real debrid file id] - transcode streaming links to various quality levels. Get the file id using unrestrict
    """.trimIndent()

    init {

        if (botToken.length > 40)
            println("Found Telegram Bot Token")
        else {
            println("[ERROR] Wrong or missing telegram bot token.\nCheck your telegram bot token with @BotFather and add it with the syntax TELEGRAM_BOT_TOKEN=...")
            exitProcess(1)
        }

        if (privateApiKey.length > 10)
            println("Found Real Debrid API Key")
        else {
            println("[ERROR] Wrong or missing real Real Debrid key.\nCheck your Real Debrid API Key and add it with the syntax PRIVATE_API_KEY=...")
            exitProcess(1)
        }

        // check temp and downloads folder
        checkAndMakeDirectories(tempPath, downloadsPath)

        println("Starting bot...")

        val bot = bot {

            token = botToken
            timeout = 30
            logLevel = when(logLevelArgument) {
                "error" -> LogLevel.Error
                "body" -> LogLevel.Network.Body
                else -> LogLevel.Error
            }

            dispatch {

                command("start") {

                    val result = bot.sendMessage(chatId = update.message!!.chat.id, text = "Bot started")

                    result.fold(
                        {
                            // do something here with the response
                        },
                        {
                            // do something with the error
                        }
                    )

                    scope.launch {
                        val user = getUser()
                        if (user != null) {
                            val welcome = "Welcome back, ${user.username}\n" +
                                    "You have ${user.premium / 60 / 60 / 24} days of premium " +
                                    "and ${user.points} points remaining."
                            bot.sendMessage(chatId = message.chat.id, text = welcome)
                        } else
                        // close the bot?
                            bot.sendMessage(
                                chatId = message.chat.id,
                                text = "Couldn't load your real debrid data\nCheck your private api key."
                            )
                    }
                }

                command("help") {
                    bot.sendMessage(chatId = message.chat.id, text = helpMessage, parseMode = ParseMode.MARKDOWN)
                }

                command("user") {

                    scope.launch {
                        val user = getUser()
                        if (user != null) {
                            val information = "${user.avatar} \n" +
                                    "id: ${user.id}\n" +
                                    "username: ${user.username}\n" +
                                    "email: ${user.email}\n" +
                                    "points: ${user.points}\n" +
                                    "type: ${user.type}\n" +
                                    "premium: ${user.premium / 60 / 60 / 24} days\n" +
                                    "expiration: ${user.expiration}"

                            bot.sendMessage(chatId = message.chat.id, text = information)
                        } else
                            bot.sendMessage(
                                chatId = message.chat.id,
                                text = "Couldn't load your real debrid user data\nCheck your private api key."
                            )
                    }
                }

                command("unrestrict") {
                    if (args.isNotEmpty()) {
                        val link = args[0]
                        when {
                            link.isWebUrl() -> {
                                scope.launch {
                                    val downloadItem = unrestrictLink(link)
                                    if (downloadItem != null) {
                                        val itemMessage: String = "*Name:* ${downloadItem.filename}\n" +
                                                "*Size:* ${downloadItem.fileSize / 1024 / 1024} MB\n" +
                                                if (downloadItem.streamable == 1) "*Streaming transcoding available using /transcode ${downloadItem.id}*\n" else "*Streaming not available*\n" +
                                                        "*Link:* ${downloadItem.download}"

                                        bot.sendMessage(
                                            chatId = message.chat.id,
                                            text = itemMessage,
                                            parseMode = ParseMode.MARKDOWN
                                        )
                                    }
                                }
                            }
                            link.isMagnet() -> {
                                scope.launch {
                                    val addedMagnet: UploadedTorrent? = addMagnet(link)
                                    if (addedMagnet != null) {
                                        val magnetMessage =
                                            "Added torrent with id ${addedMagnet.id}, check its status with /torrents"

                                        bot.sendMessage(
                                            chatId = message.chat.id,
                                            text = magnetMessage
                                        )

                                        fetchTorrentInfo(addedMagnet.id)
                                    }
                                }
                            }
                            link.isTorrent() -> {
                                val loaded = downloadTorrent(link)
                                if (loaded == true)
                                    bot.sendMessage(
                                        chatId = message.chat.id,
                                        text = "Uploading torrent to Real Debrid. Check its status with /torrents"
                                    )
                            }
                            else -> bot.sendMessage(
                                chatId = message.chat.id,
                                text = "Wrong or missing argument.\nUsage: /unrestrict [url|magnet|torrent file link]"
                            )
                        }
                    } else
                        bot.sendMessage(
                            chatId = message.chat.id,
                            text = "Wrong or missing argument.\nUsage: /unrestrict [url|magnet|torrent file link]"
                        )
                }

                command("transcode") {
                    if (args[0].isNotBlank()) {

                        scope.launch {
                            val streams: Stream? = streamLink(args[0])
                            if (streams != null) {
                                val streamsMessage = """
                                    Apple quality: ${streams.apple.link}
                                    Dash quality: ${streams.dash.link}
                                    liveMp4 quality: ${streams.liveMP4.link}
                                    h264WebM quality: ${streams.h264WebM.link}
                                """.trimIndent()

                                bot.sendMessage(
                                    chatId = message.chat.id,
                                    text = streamsMessage,
                                    parseMode = ParseMode.MARKDOWN
                                )
                            }
                        }
                    } else
                        bot.sendMessage(
                            chatId = message.chat.id,
                            text = "Wrong or missing argument.\nUsage: /stream [real debrid file id]"
                        )
                }

                command("download") {
                    // todo: restrict link to real debrid urls?
                    val link = args[0]
                    if (link.isNotBlank() && link.isWebUrl()) {
                        bot.sendMessage(
                            chatId = message.chat.id,
                            text = "Starting download"
                        )
                        "wget -P $downloadsPath $wgetArguments $link".runCommand()
                    } else
                        bot.sendMessage(
                            chatId = message.chat.id,
                            text = "Wrong or missing argument.\nUsage: /download [unrestricted link]"
                        )
                }

                command("torrents") {
                    scope.launch {
                        var retrievedTorrents = 5
                        try {
                            if (args.isNotEmpty()) {
                                val temp = Integer.parseInt(args[0])
                                retrievedTorrents = temp
                            }
                        } catch (e: NumberFormatException) {
                            bot.sendMessage(
                                chatId = message.chat.id,
                                text = "Couldn't recognize number, defaulting to 5.\nUsage: /torrents [number, default 5]"
                            )
                        }
                        val torrents: List<TorrentItem> =
                            torrentsRepository.getTorrentsList(privateApiKey, 0, 1, retrievedTorrents, null)
                        val stringBuilder = StringBuilder()
                        torrents.forEach {

                            stringBuilder.append(
                                """
                                    *Name:*  ${it.filename}
                                    *Size:*  ${it.bytes / 1024 / 1024} MB
                                    *Status:*  ${it.status}
                                    *Progress:* ${it.progress}%
                                """.trimIndent()
                            )
                            stringBuilder.append("\n")
                            if (it.links.isNotEmpty()) {
                                stringBuilder.append("*Download these files with /unrestrict:*\n")
                                it.links.forEach { link ->
                                    stringBuilder.append(link)
                                    stringBuilder.append("\n")
                                }
                            }
                            stringBuilder.append("\n")
                        }
                        bot.sendMessage(
                            chatId = message.chat.id,
                            text = stringBuilder.toString(),
                            parseMode = ParseMode.MARKDOWN
                        )
                    }
                }

                command("downloads") {
                    scope.launch {
                        var retrievedDownloads = 5
                        try {
                            if (args.isNotEmpty()) {
                                val temp = Integer.parseInt(args[0])
                                retrievedDownloads = temp
                            }
                        } catch (e: NumberFormatException) {
                            bot.sendMessage(
                                chatId = message.chat.id,
                                text = "Couldn't recognize number, defaulting to 5.\nUsage: /downloads [number, default 5]"
                            )
                        }
                        val downloads: List<DownloadItem> =
                            downloadRepository.getDownloads(privateApiKey, limit = retrievedDownloads)
                        val stringBuilder = StringBuilder()
                        downloads.forEach {
                            stringBuilder.append("*Name:*  ${it.filename}\n")
                            stringBuilder.append("*Size:*  ${it.fileSize / 1024 / 1024} MB\n")
                            stringBuilder.append(if (it.streamable == 1) "*Download/Streaming link:*  ${it.download}\n" else "*Download link:*  ${it.download}\n")
                            stringBuilder.append(if (it.streamable == 1) "*Streaming transcoding available, use /transcode ${it.id}*\n" else "*Streaming not available*\n")
                            stringBuilder.append("\n")
                        }
                        bot.sendMessage(
                            chatId = message.chat.id,
                            text = stringBuilder.toString(),
                            parseMode = ParseMode.MARKDOWN
                        )
                    }
                }
            }
        }

        bot.startPolling()

        println("Bot started")
    }

    private fun checkAndMakeDirectories(vararg paths: String) {
        // todo: add checks if path is file and if path is writeable
        paths.forEach {
            val directory = File(it)
            if (!directory.exists())
                directory.mkdir()
        }
    }

    private suspend fun getUser(): User? {
        return userRepository.getUserInfo(privateApiKey)
    }

    private suspend fun unrestrictLink(link: String): DownloadItem? {
        return unrestrictRepository.getUnrestrictedLink(privateApiKey, link)
    }

    private suspend fun streamLink(id: String): Stream? {
        return streamingRepository.getStreams(privateApiKey, id)
    }

    private suspend fun addMagnet(magnet: String): UploadedTorrent? {
        val availableHosts = torrentsRepository.getAvailableHosts(privateApiKey)
        return if (!availableHosts.isNullOrEmpty()) {
            torrentsRepository.addMagnet(privateApiKey, magnet, availableHosts.first().host)
        } else
            null
    }

    private fun fetchTorrentInfo(torrentID: String, delay: Long = 2000) {
        scope.launch {
            delay(delay)
            torrentsRepository.getTorrentInfo(privateApiKey, torrentID)?.let {
                // the download won't  start if we don't select the files
                if (it.status == "waiting_files_selection")
                    torrentsRepository.selectFiles(privateApiKey, torrentID)
                // has the magnet/torrent been loaded?
                if (initialStatusList.contains(it.status))
                    fetchTorrentInfo(it.id)
                // other statuses means the download is either started, finished or failed and require no intervention
            }
        }
    }

    private fun downloadTorrent(link: String): Boolean? {
        val downloadRequest: Request = Request.Builder().url(link).get().build()

        val response = okHttpClient.newCall(downloadRequest).execute()
        if (!response.isSuccessful) {
            return null
        }
        val source = response.body?.source()
        if (source != null) {
            val path = if (tempPath.endsWith("/")) tempPath else tempPath.plus("/") + link.hashCode() + ".torrent"
            println("Saving torrent file")
            val file = File(path)
            val bufferedSink = file.sink().buffer()
            bufferedSink.writeAll(source)
            bufferedSink.close()

            val fileInputStream = file.inputStream()
            val buffer: ByteArray = fileInputStream.readBytes()
            fileInputStream.close()

            uploadTorrent(buffer)

            return true
        } else
            return null
    }

    private fun uploadTorrent(buffer: ByteArray) {
        println("Uploading torrent to Real Debrid")
        scope.launch {
            val availableHosts = torrentsRepository.getAvailableHosts(privateApiKey)
            if (!availableHosts.isNullOrEmpty()) {
                val uploadedTorrent = torrentsRepository.addTorrent(privateApiKey, buffer, availableHosts.first().host)
                if (uploadedTorrent != null)
                    fetchTorrentInfo(uploadedTorrent.id)
            }
        }
    }
}
