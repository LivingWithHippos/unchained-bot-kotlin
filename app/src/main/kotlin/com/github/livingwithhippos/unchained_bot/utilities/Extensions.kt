package com.github.livingwithhippos.unchained_bot.utilities

import com.github.livingwithhippos.unchained_bot.MAGNET_PATTERN
import com.github.livingwithhippos.unchained_bot.TORRENT_PATTERN
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
