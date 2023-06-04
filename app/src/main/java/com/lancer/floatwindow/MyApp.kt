package com.lancer.floatwindow

import android.app.Application

class MyApp:Application() {
    companion object{
        lateinit var instance:MyApp

        fun getMyApp():MyApp{
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance=this
    }
}