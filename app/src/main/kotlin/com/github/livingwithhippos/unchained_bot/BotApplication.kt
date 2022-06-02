package com.github.livingwithhippos.unchained_bot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.inlineQuery
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.inlinequeryresults.InlineQueryResult
import com.github.kotlintelegrambot.entities.inlinequeryresults.InputMessageContent
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.livingwithhippos.unchained_bot.data.model.DownloadItem
import com.github.livingwithhippos.unchained_bot.data.model.Stream
import com.github.livingwithhippos.unchained_bot.data.model.TorrentItem
import com.github.livingwithhippos.unchained_bot.data.model.UploadedTorrent
import com.github.livingwithhippos.unchained_bot.data.model.User
import com.github.livingwithhippos.unchained_bot.data.repository.DownloadRepository
import com.github.livingwithhippos.unchained_bot.data.repository.StreamingRepository
import com.github.livingwithhippos.unchained_bot.data.repository.TorrentsRepository
import com.github.livingwithhippos.unchained_bot.data.repository.UnrestrictRepository
import com.github.livingwithhippos.unchained_bot.data.repository.UserRepository
import com.github.livingwithhippos.unchained_bot.localization.EN
import com.github.livingwithhippos.unchained_bot.localization.Localization
import com.github.livingwithhippos.unchained_bot.localization.localeMapping
import com.github.livingwithhippos.unchained_bot.utilities.isMagnet
import com.github.livingwithhippos.unchained_bot.utilities.isTorrent
import com.github.livingwithhippos.unchained_bot.utilities.isWebUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.Charset
import kotlin.system.exitProcess

class BotApplication : KoinComponent {

    // Environment variables
    private val botToken: String = getKoin().getProperty("TELEGRAM_BOT_TOKEN") ?: ""
    private val privateApiKey: String = getKoin().getProperty("PRIVATE_API_KEY") ?: ""
    private val wgetArguments: String = getKoin().getProperty("WGET_ARGUMENTS") ?: "--no-verbose"
    private val logLevelArgument: String = getKoin().getProperty("LOG_LEVEL") ?: "error"
    private val enableQuery: String = getKoin().getProperty("ENABLE_QUERIES") ?: "false"
    private val enableQueriesArgument: Boolean = enableQuery.equals("true", true) || enableQuery == "1"
    private val whitelistedUser: Long = getKoin().getProperty<String>("WHITELISTED_USER")?.toLongOrNull() ?: 0
    private val localeArgument: String = getKoin().getProperty<String>("LOCALE") ?: "en"

    private val localization: Localization = localeMapping.getOrDefault(localeArgument, EN)

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

    private val download = Job()
    private val downloadScope = CoroutineScope(Dispatchers.IO + download)

    // Filters
    // Filter the whitelisted user, if any
    private val userFilter = if (whitelistedUser > 10000) Filter.User(whitelistedUser) else Filter.All

    // Command filters
    private val startCommandFilter = Filter.Custom { text?.startsWith("/start") ?: false }
    private val helpCommandFilter = Filter.Custom { text?.startsWith("/help") ?: false }
    private val userCommandFilter = Filter.Custom { text?.startsWith("/user") ?: false }
    private val unrestrictCommandFilter = Filter.Custom { text?.startsWith("/unrestrict") ?: false }
    private val transcodeCommandFilter = Filter.Custom { text?.startsWith("/transcode ") ?: false }

    private val getCommandFilter = Filter.Custom { (text?.startsWith("/get") ?: false) }
    private val torrentsCommandFilter = Filter.Custom { text?.startsWith("/torrents") ?: false }
    private val downloadsCommandFilter = Filter.Custom { (text?.startsWith("/downloads") ?: false) }

    fun startBot() {

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

        printCurrentParameters()

        // check temp and downloads folder
        checkAndMakeDirectories(tempPath, downloadsPath)

        println("Starting bot...")

        val bot = bot {

            token = botToken
            timeout = 30
            // error when setting this, they need to update their OkHttp logging library
            logLevel = when (logLevelArgument) {
                "error" -> LogLevel.Error
                "body" -> LogLevel.Network.Body
                "basic" -> LogLevel.Network.Basic
                "headers" -> LogLevel.Network.Headers
                "none" -> LogLevel.Network.None
                else -> LogLevel.Error
            }

            // todo: add message splitting for when message length is > 4096
            dispatch {

                message(helpCommandFilter and userFilter) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = localization.helpMessage,
                        parseMode = ParseMode.MARKDOWN
                    )
                }

                message(startCommandFilter and userFilter) {

                    bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = localization.botStarted)

                    scope.launch {
                        val user = getUser()
                        if (user != null) {

                            val welcome = localization.welcomeMessage.replace(
                                "%username%", user.username
                            ).replace(
                                "%days%", (user.premium / 60 / 60 / 24).toString()
                            ).replace(
                                "%points%", user.points.toString()
                            )

                            bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = welcome)
                        } else
                        // close the bot?
                            bot.sendMessage(
                                chatId = ChatId.fromId(message.chat.id),
                                text = localization.privateKeyError
                            )
                    }
                }

                message(userCommandFilter and userFilter) {

                    scope.launch {
                        val user = getUser()
                        if (user != null) {
                            val information = "${localization.username}: ${user.username}\n" +
                                    "${localization.status}: ${user.type}\n" +
                                    "${localization.email}: ${user.email}\n" +
                                    "${localization.points}: ${user.points}\n" +
                                    "${localization.premium}: ${user.premium / 60 / 60 / 24} ${localization.days}\n" +
                                    "${localization.expiration}: ${user.expiration}" +
                                    "${localization.id}: ${user.id}\n" +
                                    "${user.avatar} \n"

                            bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = information)
                        } else
                            bot.sendMessage(
                                chatId = ChatId.fromId(message.chat.id),
                                text = localization.privateKeyError
                            )
                    }
                }

                message(unrestrictCommandFilter and userFilter) {
                    val args = getArgAsString(message.text)
                    if (!args.isNullOrBlank()) {
                        when {
                            args.isWebUrl() -> {
                                scope.launch {
                                    val downloadItem = unrestrictLink(args)
                                    if (downloadItem != null) {
                                        val itemMessage: String =
                                            formatDownloadItem(downloadItem, allowTranscoding = true)

                                        bot.sendMessage(
                                            chatId = ChatId.fromId(message.chat.id),
                                            text = itemMessage,
                                            parseMode = ParseMode.MARKDOWN
                                        )
                                    }
                                }
                            }
                            args.isMagnet() -> {
                                scope.launch {
                                    val addedMagnet: UploadedTorrent? = addMagnet(args)
                                    if (addedMagnet != null) {
                                        val magnetMessage = localization.addedTorrent.replace("%id%", addedMagnet.id)

                                        bot.sendMessage(
                                            chatId = ChatId.fromId(message.chat.id),
                                            text = magnetMessage
                                        )

                                        fetchTorrentInfo(addedMagnet.id)
                                    }
                                }
                            }
                            args.isTorrent() -> {
                                val loaded = downloadTorrent(args)
                                if (loaded)
                                    bot.sendMessage(
                                        chatId = ChatId.fromId(message.chat.id),
                                        text = localization.uploadingTorrent
                                    )
                            }
                            else -> bot.sendMessage(
                                chatId = ChatId.fromId(message.chat.id),
                                text = localization.wrongUnrestrictSyntax
                            )
                        }
                    } else
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = localization.wrongUnrestrictSyntax
                        )
                }

                message(transcodeCommandFilter and userFilter) {
                    val args = getArgAsString(message.text)
                    if (!args.isNullOrBlank()) {

                        scope.launch {
                            val streams: Stream? = streamLink(args)
                            if (streams != null) {
                                val streamsMessage = """
                                    ${localization.appleQuality}: ${streams.apple.link}
                                    ${localization.dashQuality}: ${streams.dash.link}
                                    ${localization.liveMP4Quality}: ${streams.liveMP4.link}
                                    ${localization.h264WebMQuality}: ${streams.h264WebM.link}
                                """.trimIndent()

                                bot.sendMessage(
                                    chatId = ChatId.fromId(message.chat.id),
                                    text = streamsMessage,
                                    parseMode = ParseMode.MARKDOWN
                                )
                            }
                        }
                    } else
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = localization.wrongStreamSyntax
                        )
                }

                message(getCommandFilter and userFilter) {
                    val args = getArgAsString(message.text)
                    // todo: restrict link to real debrid urls?
                    if (!args.isNullOrBlank() && args.isWebUrl()) {
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = localization.startingDownload
                        )
                        downloadScope.launch {
                            withContext(Dispatchers.IO) {
                                val process = ProcessBuilder(
                                    "wget",
                                    "-P", downloadsPath,
                                    wgetArguments,
                                    args
                                ).redirectOutput(ProcessBuilder.Redirect.PIPE)
                                    .start()
                                val reader = process.inputStream.bufferedReader(Charset.defaultCharset())
                                reader.use {
                                    var line = it.readLine()
                                    while (line != null) {
                                        println(line)
                                        line = it.readLine()
                                        if (line != null)
                                            bot.sendMessage(
                                                chatId = ChatId.fromId(message.chat.id),
                                                text = line
                                            )
                                    }
                                }
                                process.waitFor()
                            }
                        }
                    } else
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = localization.wrongDownloadSyntax
                        )
                }

                message(torrentsCommandFilter and userFilter) {
                    val args = getArgAsInt(message.text)
                    scope.launch {
                        val retrievedTorrents = args ?: 5
                        val torrents: List<TorrentItem> =
                            torrentsRepository.getTorrentsList(privateApiKey, 0, 1, retrievedTorrents, null)
                        val stringBuilder = StringBuilder()
                        torrents.forEach {

                            val tempBuffer = StringBuffer()

                            tempBuffer.append(
                                """
                                    ${localization.name}:  ${it.filename}
                                    ${localization.size}:  ${it.bytes / 1024 / 1024} MB
                                    ${localization.status}:  ${it.status}
                                    ${localization.progress}: ${it.progress}%
                                """.trimIndent()
                            )
                            tempBuffer.appendLine()
                            if (it.links.isNotEmpty()) {
                                if (it.links.size == 1) {
                                    tempBuffer.append("${localization.getDownloadLink}\n/unrestrict ${it.links.first()}\n")
                                } else {
                                    tempBuffer.append("${localization.getDownloadLink}\n")
                                    it.links.forEach { link ->
                                        tempBuffer.append("/unrestrict ")
                                        tempBuffer.append(link)
                                        tempBuffer.appendLine()
                                    }
                                }
                            }
                            tempBuffer.appendLine()

                            // if the size of the message is too big send a message and clear the main builder
                            if (stringBuilder.length + tempBuffer.length > 4000) {
                                bot.sendMessage(
                                    chatId = ChatId.fromId(message.chat.id),
                                    text = stringBuilder.toString(),
                                    disableWebPagePreview = true
                                )
                                stringBuilder.clear()
                            }
                            // add the current message to the main builder
                            stringBuilder.append(tempBuffer)
                        }
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = stringBuilder.toString(),
                            disableWebPagePreview = true
                        )
                    }
                }

                message(downloadsCommandFilter and userFilter) {
                    val args = getArgAsInt(message.text)
                    scope.launch {
                        val retrievedDownloads = args ?: 5
                        val downloads: List<DownloadItem> =
                            downloadRepository.getDownloads(privateApiKey, limit = retrievedDownloads)
                        val stringBuilder = StringBuilder()
                        downloads.forEach {
                            val tempBuffer = StringBuilder()
                            tempBuffer.append(formatDownloadItem(it))
                            tempBuffer.appendLine()
                            tempBuffer.appendLine()

                            if (stringBuilder.length + tempBuffer.length > 4000) {
                                bot.sendMessage(
                                    chatId = ChatId.fromId(message.chat.id),
                                    text = stringBuilder.toString(),
                                    parseMode = ParseMode.MARKDOWN
                                )
                                stringBuilder.clear()
                            }
                            stringBuilder.append(tempBuffer)
                        }
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = stringBuilder.toString(),
                            parseMode = ParseMode.MARKDOWN
                        )
                    }
                }

                // todo: add torrents and magnet support, add downloads and torrents list from the menu as items if the query is empty
                // decide what to do with authentication, use env variable
                if (enableQueriesArgument) {
                    // N.B: you need to enable the inlining with BotFather using `/setinline` to use this
                    inlineQuery {
                        val queryText = inlineQuery.query

                        if (queryText.isBlank() or queryText.isEmpty()) return@inlineQuery

                        if (!queryText.isWebUrl()) return@inlineQuery

                        scope.launch {
                            val downloadItem = unrestrictLink(queryText)
                            if (downloadItem != null) {
                                val itemMessage: String = formatDownloadItem(downloadItem)

                                val inlineResults = listOf(
                                    InlineQueryResult.Article(
                                        id = "1",
                                        title = localization.unrestrict,
                                        inputMessageContent = InputMessageContent.Text(
                                            itemMessage,
                                            parseMode = ParseMode.MARKDOWN
                                        ),
                                        description = localization.unrestrictDescription,
                                        url = null
                                    )
                                )

                                bot.answerInlineQuery(inlineQuery.id, inlineResults)

                            } else {
                                val inlineResults = listOf(
                                    InlineQueryResult.Article(
                                        id = "1",
                                        title = localization.error,
                                        inputMessageContent = InputMessageContent.Text(
                                            localization.unrestrictError,
                                            parseMode = ParseMode.MARKDOWN
                                        ),
                                        description = localization.unrestrictError,
                                        url = null
                                    )
                                )

                                bot.answerInlineQuery(inlineQuery.id, inlineResults)
                            }
                        }
                    }
                }
            }
        }

        bot.startPolling()

        println(localization.botStarted)
    }

    private fun printCurrentParameters() {
        println(
            """
            ******************
            * BOT PARAMETERS *
            ******************
        """.trimIndent()
        )
        println("Wget arguments: $wgetArguments")
        println("Log level: $logLevelArgument")
        println("Queries enabled: $enableQueriesArgument")
        val hasUser = whitelistedUser > 1000
        println("Whitelisted user: $hasUser")
        println("Localization: $localeArgument")
        println("Temp file path: $tempPath")
        println("Downloaded files path: $downloadsPath \n")
    }

    private fun formatDownloadItem(item: DownloadItem, allowTranscoding: Boolean = false): String {
        // todo: add keyboard to launch transcoding instructions
        return "*${localization.name}:* ${item.filename}\n" +
                "*${localization.size}:* ${item.fileSize / 1024 / 1024} MB\n" +
                (
                        if (allowTranscoding) {
                            if (item.streamable == 1)
                                "${localization.transcodingInstructions} /transcode ${item.id}\n"
                            else
                                "*${localization.streamingUnavailable}*\n"
                        } else
                            ""
                        ) +
                "*${localization.link}:* ${item.download}"
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

    private fun downloadTorrent(link: String): Boolean {
        val downloadRequest: Request = Request.Builder().url(link).get().build()

        val response = okHttpClient.newCall(downloadRequest).execute()
        if (!response.isSuccessful) {
            return false
        }
        val source = response.body?.source()
        if (source != null) {
            val path = (if (tempPath.endsWith("/")) tempPath else tempPath.plus("/")) + link.hashCode() + ".torrent"
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
            return false
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

    private fun getArgAsInt(args: String?): Int? {
        return args?.split("\\s+".toRegex())?.drop(1)?.firstOrNull()?.toIntOrNull()
    }

    private fun getArgsAsList(args: String?): List<String> {
        return args?.split("\\s+".toRegex())?.drop(1) ?: emptyList()
    }

    private fun getArgAsString(args: String?): String? {
        return args?.split("\\s+".toRegex())?.drop(1)?.firstOrNull()
    }
}
