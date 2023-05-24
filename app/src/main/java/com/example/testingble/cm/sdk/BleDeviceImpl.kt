package com.example.testingble.cm.sdk

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.example.testingble.cm.api.BleDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class BleDeviceImpl(private val bluetoothDevice: BluetoothDevice) : BleDevice {

    override val address: String
        get() = bluetoothDevice.address

    override val deviceName: String
        @SuppressLint("MissingPermission")
        get() = bluetoothDevice.name ?: ""

    private val _pairingState = MutableStateFlow(0)
    override val pairingState = _pairingState.asStateFlow()

    private val _connectionState = MutableStateFlow(0)
    override val connectionState = _connectionState.asStateFlow()
}