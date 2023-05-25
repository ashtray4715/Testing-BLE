package com.example.testingble.cm.sdk

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.content.Context
import com.example.testingble.cm.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class BleDeviceImpl(
    val mContext: Context,
    val mBluetoothDevice: BluetoothDevice
) : BleDevice {

    val mCoroutineScope = CoroutineScope(Dispatchers.IO)

    val mTag = "BleDeviceImpl:${mBluetoothDevice.address.substring(0..4)}"

    var mBluetoothGatt: BluetoothGatt? = null

    override val address: String
        get() = mBluetoothDevice.address

    override val deviceName: String
        @SuppressLint("MissingPermission")
        get() = mBluetoothDevice.name ?: ""

    @SuppressLint("MissingPermission")
    val mPairingState = MutableStateFlow(
        when (mBluetoothDevice.bondState == BluetoothDevice.BOND_BONDED) {
            true -> PairingState.PAIRED
            else -> PairingState.NOT_PAIRED
        }
    )
    override val pairingState = mPairingState.asStateFlow()

    val mConnectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState = mConnectionState.asStateFlow()

    val mGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            doOnConnectionStateChanged(newState)
        }
    }

    override fun connect(): DoConnectResult {
        return doConnect()
    }

    override fun disconnect(): DoDisconnectResult {
        return doDisconnect()
    }

    override fun sendData(): SendDataResult {
        return SendDataResult.RESULT_OK // todo - need to implement
    }
}