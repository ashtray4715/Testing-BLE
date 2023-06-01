package com.example.testingble.cm.sdk

import android.content.Context
import com.example.testingble.cm.api.DeviceManagerApi
import com.example.testingble.cm.api.DiscoveryManagerApi
import com.example.testingble.cm.api.ModuleNotInitializedException
import com.example.testingble.cm.sdk.permission.PermissionManager

class BluetoothSdk {

    private var discoveryManagerApi: DiscoveryManagerApi? = null
    private var deviceManagerApi: DeviceManagerApi? = null

    fun initSdk(context: Context) {
        discoveryManagerApi = DiscoveryManager(context, PermissionManager())
        deviceManagerApi = DeviceManager(context)
    }

    fun getDiscoveryManager(): DiscoveryManagerApi {
        return discoveryManagerApi ?: throw ModuleNotInitializedException()
    }

    fun getDeviceManager(): DeviceManagerApi {
        return deviceManagerApi ?: throw ModuleNotInitializedException()
    }

    fun destroySdk() {
        discoveryManagerApi = null
        deviceManagerApi = null
    }
}