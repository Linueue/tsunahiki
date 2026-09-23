package com.kldevs.tsunahiki

import android.app.Application
import com.kldevs.tsunahiki.audio.initKoin
import org.koin.android.ext.koin.androidContext

class AndroidApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@AndroidApplication)
        }
    }
}