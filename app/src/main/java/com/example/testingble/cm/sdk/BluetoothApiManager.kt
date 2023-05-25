package com.example.testingble.cm.sdk

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object BluetoothApiManager {
    @SuppressLint("MissingPermission")
    suspend fun connect(bleDeviceImpl: BleDeviceImpl): BluetoothGatt {
        return withContext(Dispatchers.IO) {
            bleDeviceImpl.mBluetoothDevice.connectGatt(
                bleDeviceImpl.mContext,
                false,
                bleDeviceImpl.mGattCallback
            )
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun disconnect(gatt: BluetoothGatt) {
        return withContext(Dispatchers.IO) {
            gatt.disconnect()
        }
    }
}