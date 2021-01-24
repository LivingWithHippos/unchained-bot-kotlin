package com.github.livingwithhippos.unchained_bot.utilities

import com.github.livingwithhippos.unchained_bot.MAGNET_PATTERN
import com.github.livingwithhippos.unchained_bot.TORRENT_PATTERN
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * check if a String is an url
 */

fun String.isWebUrl(): Boolean = Patterns.WEB_URL.matcher(this).matches()

/**
 * check if a String is a magnet link
 */
fun String?.isMagnet(): Boolean {
    if (this == null)
        return false
    val m: Matcher = Pattern.compile(MAGNET_PATTERN).matcher(this)
    return m.lookingAt()
}

/**
 * check if a String is a torrent link
 */
fun String?.isTorrent(): Boolean {
    if (this == null)
        return false
    val m: Matcher = Pattern.compile(TORRENT_PATTERN).matcher(this)
    return m.matches()
}

/**
 * execute a string as a command in the shell. Redirect output to stderr
 */
fun String.runCommand(workingDir: File? = null) {
    val process = ProcessBuilder(*split(" ").toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
    // unnecessary for a download manager
    /*
    if (!process.waitFor(10, TimeUnit.SECONDS)) {
        process.destroy()
        throw RuntimeException("execution timed out: $this")
    }
    if (process.exitValue() != 0) {
        // we don't care if the download manager has issues
        throw RuntimeException("execution failed with code ${process.exitValue()}: $this")
        //println("execution failed with code ${process.exitValue()}: $this")
    }
     */
}

/**
 * execute a string as a command in the shell. Redirect output to a String
 */
fun String.runCommandWithOutput(workingDir: File): String? {
    try {
        val parts = this.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        proc.waitFor(60, TimeUnit.MINUTES)
        return proc.inputStream.bufferedReader().readText()
    } catch(e: IOException) {
        e.printStackTrace()
        return null
    }
}
