package com.example.testingble.cm.sdk

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.example.testingble.cm.api.DiscoveredDevice

internal class DiscoveredDeviceImpl(private val bluetoothDevice: BluetoothDevice) : DiscoveredDevice {
    override val name: String
        @SuppressLint("MissingPermission")
        get() = bluetoothDevice.name ?: ""

    override val address: String
        get() = bluetoothDevice.address
}