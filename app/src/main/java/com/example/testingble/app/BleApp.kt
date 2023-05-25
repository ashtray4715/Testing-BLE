package com.example.testingble.app

import android.app.Application
import com.example.testingble.cm.sdk.BluetoothSdk

class BleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        sdk.initSdk(this)
    }

    companion object {
        private val sdk = BluetoothSdk()
        fun getSdk(): BluetoothSdk = sdk
    }
}