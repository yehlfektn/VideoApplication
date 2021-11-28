package com.example.videoapplication

import android.app.Application
import com.example.videoapplication.di.*
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import kz.laccent.util.debug
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class VideoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //Init Logger
        Logger.addLogAdapter(AndroidLogAdapter())
        //Init Koin
        startKoin {
            androidContext(this@VideoApplication)
            debug {
                androidLogger()
            }
            modules(listOf(appModule, mainModule, networkModule))
        }
    }
}