/*
 * Aurora Store
 *  Copyright (C) 2021, Rahul Kumar Patel <whyorean@gmail.com>
 *
 *  Aurora Store is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Aurora Store is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aurora Store.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.wineberryhalley.upthunder.api

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.aurora.gplayapi.data.models.AuthData
import com.aurora.store.data.downloader.DownloadManager
import com.aurora.store.data.network.HttpClient
import com.aurora.store.data.providers.NetworkProvider
import com.aurora.store.data.receiver.PackageManagerReceiver
import com.aurora.store.util.PackageUtil
import com.google.gson.Gson
import com.tonyodev.fetch2.Fetch
import com.wineberryhalley.upthunder.api.util.CommonUtil
import com.wineberryhalley.upthunder.updater.AuthGPlay
import com.wineberryhalley.upthunder.updater.StrDt
import nl.komponents.kovenant.android.startKovenant
import nl.komponents.kovenant.android.stopKovenant
import nl.komponents.kovenant.task

class AuroraApplication : MultiDexApplication() {

    private lateinit var fetch: Fetch
    private lateinit var packageManagerReceiver: PackageManagerReceiver

    companion object{
        val enqueuedInstalls: MutableSet<String> = mutableSetOf()

        fun buildSecureAnonymousAuthData(onResponseAuth: AuthGPlay.OnResponseAuth) {

            task {
                var properties = StrDt.getRandom()


                val gson = Gson()

                val playResponse = HttpClient
                        .getPreferredClient()
                        .postAuth(
                                Constants.URL_DISPENSER,
                                gson.toJson(properties).toByteArray()
                        )

                Log.e("MAIN", "loge: "+playResponse.code+" "+playResponse.isSuccessful )
                if (playResponse.isSuccessful) {
                    return@task gson.fromJson(
                            String(playResponse.responseBytes),
                            AuthData::class.java
                    )
                } else {
                    when (playResponse.code) {
                        404 -> throw Exception("Server unreachable")
                        429 -> throw Exception("Oops, You are rate limited")
                        else -> throw Exception(playResponse.errorString)
                    }
                }
            } success {
                //Set AuthData as anonymous
                StrDt.saveAuthAnom(it)
onResponseAuth.OnSuccess()
            } fail {
                onResponseAuth.OnFail(it.message)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)


        fetch = DownloadManager.with(this).fetch

        packageManagerReceiver = object : PackageManagerReceiver() {

        }

        //Register broadcast receiver for package install/uninstall
        registerReceiver(packageManagerReceiver, PackageUtil.getFilter())

        NetworkProvider
            .with(this)
            .bind()

        startKovenant()

        CommonUtil.cleanupInstallationSessions(applicationContext)
    }

    override fun onTerminate() {
        NetworkProvider
            .with(this)
            .unbind()
        stopKovenant()
        super.onTerminate()
    }



    override fun onLowMemory() {
        NetworkProvider
            .with(this)
            .unbind()
        super.onLowMemory()
    }
}