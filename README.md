# unchained-bot-kotlin

Telegram Bot written in Kotlin for Real-Debrid.

Unchained Bot Kotlin is a [Telegram Bot](https://core.telegram.org/bots) that allows you to interface with [Real-Debrid](https://real-debrid.com/). If you want to support me, you can instead click through [this referral link](http://real-debrid.com/?id=78841).

My [previous bot](https://github.com/LivingWithHippos/unchained-bot) was written in Python, but I realized that since [Unchained for Android](https://github.com/LivingWithHippos/unchained-android) was much more completed and a telegram library for python was available I could port the application with minimal effort. In fact around 90% of the code is shared with Unchained for Android.

## Installation

### Docker (recommended)

An official image is being worked on. At the moment is possible to clone the repository and build an image from the Dockerfile with `docker build -t livingwithhippos/unchainedbot:0.1 .` and run it with either docker cli o docker compose.

#### Docker compose

```yaml
version: '3'

services:
  unchained:
    image: livingwithhippos/unchainedbot:0.1
    container_name: unchainedbot
    restart: unless-stopped
    environment:
      - TELEGRAM_BOT_TOKEN=${BOT_TOKEN}
      - PRIVATE_API_KEY=${API_KEY}
      # optional
      # - WGET_ARGUMENTS=see https://www.gnu.org/software/wget/manual/wget.html, default is "--no-verbose"
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
  -e WGET_ARGUMENTS= `#optional` \
  -e LOG_LEVEL= `#optional` \
  -v ./downloads:/downloads \
  --restart unless-stopped \
  livingwithhippos/unchainedbot:0.1
```

### As a Kotlin/java application

After cloning the project, navigate to the `app` folder and run `./gradlew Jar`. This will generate the file `unchained-bot-kotlin/app/build/libs/unchained-bot-kotlin.jar`. You can also download the `unchained-bot-kotlin.jar` file from the release page. You can move the file somewhere and run the bot with `java -DTELEGRAM_BOT_TOKEN=your-token -DPRIVATE_API_KEY=your-key -DDOWNLOADS_PATH=your-download-folder -jar unchained-bot-kotlin.jar`

If you don't use docker or already have java installed, this file is just ~ 9 MB.

### Parameters

| Parameter | Function |
|---|---|
| -e TELEGRAM_BOT_TOKEN | get your telegram token from https://core.telegram.org/bots#3-how-do-i-create-a-bot |
| -e PRIVATE_API_KEY | get your private API key from https://real-debrid.com/apitoken |
| -e WGET_ARGUMENTS | wget is used to download files locally. Pass arguments to it with this |
| -e LOG_LEVEL | default is error, if you have issues you can set this to another level like body, basic, headers, none |
| -e TEMP_PATH | path where temporary files, such are `.torrent` files, are being downloaded. You probably won't change this. |
| -e DOWNLOADS_PATH | the folder where files are downloaded with `/download`. If you're using docker just change the mounted folder instead: `/new/path:/downloads` |