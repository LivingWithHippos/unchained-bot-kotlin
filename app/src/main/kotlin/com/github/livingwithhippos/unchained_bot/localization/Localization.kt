package com.github.livingwithhippos.unchained_bot.localization

interface Localization {
    val helpMessage: String
}

val localeMapping: Map<String, Localization> = mapOf (
    Pair("en", EN),
    Pair("it", IT)
)