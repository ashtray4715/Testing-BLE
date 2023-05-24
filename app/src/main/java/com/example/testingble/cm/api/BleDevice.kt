package com.example.testingble.cm.api

import kotlinx.coroutines.flow.StateFlow

interface BleDevice {
    val address: String
    val deviceName: String
    val pairingState: StateFlow<Int>
    val connectionState: StateFlow<Int>
}