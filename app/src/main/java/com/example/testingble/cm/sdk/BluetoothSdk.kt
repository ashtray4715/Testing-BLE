package com.example.testingble.cm.sdk

import android.content.Context
import com.example.testingble.cm.api.DeviceManagerApi
import com.example.testingble.cm.api.DiscoveryManagerApi

class BluetoothSdk(context: Context) {

    val discoveryManagerApi: DiscoveryManagerApi = DiscoveryManager(context)
    val deviceManagerApi: DeviceManagerApi = DeviceManager(context)

    fun initSdk() {

    }

    fun destroySdk() {

    }
}