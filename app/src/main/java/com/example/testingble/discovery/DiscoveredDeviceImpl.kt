package com.example.testingble.discovery

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice

class DiscoveredDeviceImpl(private val bluetoothDevice: BluetoothDevice) : DiscoveredDevice {
    override val name: String
        @SuppressLint("MissingPermission")
        get() = bluetoothDevice.name ?: ""

    override val address: String
        get() = bluetoothDevice.address
}