package com.example.testingble.cm.api

import kotlinx.coroutines.flow.StateFlow

interface BleDevice {
    val address: String
    val deviceName: String
    val pairingState: StateFlow<PairingState>
    val connectionState: StateFlow<ConnectionState>

    fun connect(): DoConnectResult
    fun disconnect(): DoDisconnectResult
    fun sendData(): SendDataResult
}