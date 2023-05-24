package com.example.testingble

import android.app.Application
import com.example.testingble.cm.sdk.BluetoothSdk

class BleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        sdk = BluetoothSdk(this)
    }

    companion object {
        private lateinit var sdk: BluetoothSdk
        fun getSdk(): BluetoothSdk = sdk
    }
}