package com.example.testingble.cm.api

interface DeviceManagerApi {
    fun getDevice(address: String): BleDevice
    fun getPairedDeviceList(): List<String>
}