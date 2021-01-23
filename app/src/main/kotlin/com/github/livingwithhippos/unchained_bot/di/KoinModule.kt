package com.github.livingwithhippos.unchained_bot.di

import com.github.livingwithhippos.unchained.data.repositoy.DownloadRepository
import com.github.livingwithhippos.unchained_bot.data.remote.DownloadApiHelper
import com.github.livingwithhippos.unchained_bot.data.remote.DownloadApiHelperImpl
import com.github.livingwithhippos.unchained_bot.data.remote.DownloadsApi
import com.github.livingwithhippos.unchained_bot.data.remote.StreamingApi
import com.github.livingwithhippos.unchained_bot.data.remote.StreamingApiHelper
import com.github.livingwithhippos.unchained_bot.data.remote.StreamingApiHelperImpl
import com.github.livingwithhippos.unchained_bot.data.remote.TorrentApiHelper
import com.github.livingwithhippos.unchained_bot.data.remote.TorrentApiHelperImpl
import com.github.livingwithhippos.unchained_bot.data.remote.TorrentsApi
import com.github.livingwithhippos.unchained_bot.data.remote.UnrestrictApi
import com.github.livingwithhippos.unchained_bot.data.remote.UnrestrictApiHelper
import com.github.livingwithhippos.unchained_bot.data.remote.UnrestrictApiHelperImpl
import com.github.livingwithhippos.unchained_bot.data.remote.UserApi
import com.github.livingwithhippos.unchained_bot.data.remote.UserApiHelper
import com.github.livingwithhippos.unchained_bot.data.remote.UserApiHelperImpl
import com.github.livingwithhippos.unchained_bot.data.repository.CredentialsRepository
import com.github.livingwithhippos.unchained_bot.data.repository.StreamingRepository
import com.github.livingwithhippos.unchained_bot.data.repository.TorrentsRepository
import com.github.livingwithhippos.unchained_bot.data.repository.UnrestrictRepository
import com.github.livingwithhippos.unchained_bot.data.repository.UserRepository
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit

val myModules = module {

    single<Moshi> { MoshiAdapter.getBuilder() }
    single<CredentialsRepository> { CredentialsRepository(get()) }
    single<OkHttpClient> { ApiFactory.provideOkHttpClient() }
    single<Retrofit> { ApiFactory.apiRetrofit(get()) }

    single<UserApi> { ApiFactory.provideUserApi(get()) }
    single<UserApiHelper> { UserApiHelperImpl(get()) }
    single<UserRepository> { UserRepository(get()) }

    single<UnrestrictApi> { ApiFactory.provideUnrestrictApi(get()) }
    single<UnrestrictApiHelper> { UnrestrictApiHelperImpl(get()) }
    single<UnrestrictRepository> { UnrestrictRepository(get()) }

    single<StreamingApi> { ApiFactory.provideStreamingApi(get()) }
    single<StreamingApiHelper> { StreamingApiHelperImpl(get()) }
    single<StreamingRepository> { StreamingRepository(get()) }

    single<TorrentsApi> { ApiFactory.provideTorrentsApi(get()) }
    single<TorrentApiHelper> { TorrentApiHelperImpl(get()) }
    single<TorrentsRepository> { TorrentsRepository(get()) }

    single<DownloadsApi> { ApiFactory.provideDownloadsApi(get()) }
    single<DownloadApiHelper> { DownloadApiHelperImpl(get()) }
    single<DownloadRepository> { DownloadRepository(get()) }
}
