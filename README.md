# unchained-bot-kotlin

Telegram Bot written in Kotlin for Real-Debrid.

Unchained Bot Kotlin is a [Telegram Bot](https://core.telegram.org/bots) that allows you to interface with [Real-Debrid](https://real-debrid.com/). If you want to support me, you can instead click through [this referral link](http://real-debrid.com/?id=78841).

My [previous bot](https://github.com/LivingWithHippos/unchained-bot) was written in Python, but I realized that since [Unchained for Android](https://github.com/LivingWithHippos/unchained-android) was much more completed and a [telegram library for Kotlin](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot) was available I could port the application with minimal effort. In fact around 90% of the code is shared with Unchained for Android.

- [unchained-bot-kotlin](#unchained-bot-kotlin)
  * [Installation](#installation)
    + [Docker (recommended)](#docker--recommended-)
      - [Docker compose](#docker-compose)
      - [Docker cli](#docker-cli)
    + [As a Kotlin/java application](#as-a-kotlin-java-application)
    + [Parameters](#parameters)
  * [Available Commands](#available-commands)

## Installation

### Docker (recommended)

An official image is available on [dockerhub](https://hub.docker.com/r/livingwithhippos/unchainedbotkotlin). It is also possible to clone the repository and build an image from the Dockerfile with `docker build -t livingwithhippos/unchainedbot:0.1 .` and run it with either docker cli o docker compose.

#### Docker compose

```yaml
version: '3'

services:
  unchained:
    image: livingwithhippos/unchainedbotkotlin:latest
    container_name: unchainedbot
    restart: unless-stopped
    environment:
      - TELEGRAM_BOT_TOKEN=${BOT_TOKEN}
      - PRIVATE_API_KEY=${API_KEY}
      # optional
      # only let this user use the bot
      # - WHITELISTED_USER=your telegram user id
      # add arguments for wget, userd for /get
      # - WGET_ARGUMENTS=see https://www.gnu.org/software/wget/manual/wget.html, default is "--no-verbose"
      # OkHttp log level
      # - LOG_LEVEL=availabel options are error, body, basic, headers, none. Default is error
    volumes:
      - ./downloads:/downloads
```

Copy or move the `.env.sample` file to `.env` and add the necessary arguments.

#### Docker cli

```shell
docker run -d \
  --name=unchainedbot \
  -e TELEGRAM_BOT_TOKEN=abc `#required` \
  -e PRIVATE_API_KEY=def `#required` \
  -e WHITELISTED_USER= `#optional` \
  -e WGET_ARGUMENTS= `#optional` \
  -e LOG_LEVEL= `#optional` \
  -v ./downloads:/downloads \
  --restart unless-stopped \
  livingwithhippos/unchainedbotkotlin:latest
```

#### Other optional volumes

It is possible to mount the log file for wget and the config file for wget.


### As a java application

After cloning the project, navigate to the `app` folder and run `./gradlew Jar`. This will generate the file `unchained-bot-kotlin/app/build/libs/unchained-bot-kotlin.jar`. You can also download the `unchained-bot-kotlin.jar` file from the release page.

You can then move the file somewhere and run the bot with 

```shell
java \
-DTELEGRAM_BOT_TOKEN=your-token \
-DPRIVATE_API_KEY=your-key \
-DDOWNLOADS_PATH=your-download-folder \
-jar unchained-bot-kotlin.jar
```

To use the `/get` command you need wget installed.

If you don't use docker or already have java installed, this file is just ~ 9 MB. The docker image is around 230 MB.

### Parameters

| Parameter | Function                                                                                                                                                            |
|---|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| TELEGRAM_BOT_TOKEN | Required. Get your telegram token from https://core.telegram.org/bots#3-how-do-i-create-a-bot                                                                       |
| PRIVATE_API_KEY | Required. Get your private API key from https://real-debrid.com/apitoken                                                                                            |
| WHITELISTED_USER | let only this user utilize the bot. Needs the user's telegram ID.                                                                                                   |
| ENABLE_QUERIES | Lets you mention the bot with a `@botName link` syntax from any chat to unrestrict  a link. NOT RESTRICTED TO WHITELISTED USER!!! Values "true"or "false" (default) |
| LOCALE | Set the current translation. The values "en" for english and "it" for italian are available                                                                         |
| WGET_ARGUMENTS | wget is used to download files locally. Pass arguments to it with this                                                                                              |
| LOG_LEVEL | default is error, if you have issues you can set this to another level like body, basic, headers, none (currently bugged, do not use)                               |
| TEMP_PATH | path where temporary files, such are `.torrent` files, are being downloaded. You probably won't change this.                                                        |
| DOWNLOADS_PATH | the folder where files are downloaded with `/get`. If you're using docker just change the mounted folder instead: `/new/path:/downloads`                            |

## Available Commands

Parameters between [square brackets] are optional.

| Parameter                      | Function |
|--------------------------------|---|
| /help                          | display the list of available commands |
| /user                          | get Real Debrid user's information |
| /torrents [number, default 5]  | list the last torrents |
| /downloads [number, default 5] | list the last downloads |
| /get unrestricted_link         | downloads the link on the directory of the server running the bot |
| /unrestrict url                |magnet|torrent file link | generate a download link. Magnet/Torrents will be queued, check their status with /torrents |
| /transcode real_debrid_file_id | transcode streaming links to various quality levels. Get the file id using /unrestrict |

## Thanks, Mr. Unchained
<a href="https://imgbb.com/"><img src="https://i.ibb.co/grzjQsT/Oliva.jpg" width=300 alt="Mr. Unchained" border="0"></a>