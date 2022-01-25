package com.github.livingwithhippos.unchained_bot

import com.github.livingwithhippos.unchained_bot.di.myModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.environmentProperties

fun main(args: Array<String>) {

    val koinInstance = startKoin {
        // enable Printlogger with default Level.INFO
        // can have Level & implementation
        // equivalent to logger(Level.DEBUG, PrintLogger())
        printLogger(Level.ERROR)

        // declare properties from given map
        // properties()

        // load properties from koin.properties file or given file name
        // fileProperties()

        // load properties from environment
        // use this to load docker env variables, use getProperty("bot_token")?:"default_value"
        environmentProperties()

        // list all used modules
        // as list or vararg
        modules(myModules)
    }

    val botApp = BotApplication()
    botApp.startBot()
}
