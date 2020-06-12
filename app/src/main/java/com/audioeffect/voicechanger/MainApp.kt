package com.audioeffect.voicechanger

import android.app.Application
import com.audioeffect.voicechanger.utils.AppBilling
import com.orhanobut.hawk.Hawk
import io.sentry.Sentry
import io.sentry.android.AndroidSentryClientFactory

class MainApp : Application() {
    var instance: MainApp? = null

    override fun onCreate() {
        super.onCreate()
        instance = this

        Sentry.init(MobileApp.dns(this), AndroidSentryClientFactory(this))

        MobileBilling.init(AppBilling::class.java)

        Hawk.init(this).build()
    }
}