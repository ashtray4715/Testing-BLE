package com.example.testingble.cm.sdk

import android.annotation.SuppressLint
import android.bluetooth.BluetoothProfile
import com.example.testingble.cm.api.ConnectionState
import com.example.testingble.cm.api.DoConnectResult
import com.example.testingble.cm.api.DoDisconnectResult
import com.example.testingble.cm.api.PairingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@SuppressLint("MissingPermission")
internal fun BleDeviceImpl.doConnect(): DoConnectResult {
    Timber.i("$mTag doConnect: invoked currentState: ${connectionState.value}")
    if (permissionManager.hasBluetoothConnectPermission(mContext).not()) {
        Timber.e("$mTag doConnect: invoked but has missing permissions")
        return DoConnectResult.ERROR_MISSING_PERMISSION
    }
    if (pairingState.value != PairingState.NOT_PAIRED) {
        Timber.e("$mTag doConnect: invoked but device is not paired")
        return DoConnectResult.ERROR_NOT_PAIRED_DEVICE
    }
    return when (connectionState.value) {
        ConnectionState.CONNECTED -> {
            Timber.e("$mTag doConnect: invoked but device is already connected")
            DoConnectResult.ERROR_ALREADY_CONNECTED
        }
        ConnectionState.CONNECTING -> {
            Timber.e("$mTag doConnect: invoked but device is already connecting")
            DoConnectResult.ERROR_ALREADY_CONNECTING
        }
        ConnectionState.DISCONNECTING -> {
            Timber.e("$mTag doConnect: invoked but device is getting disconnected")
            DoConnectResult.ERROR_GETTING_DISCONNECTED
        }
        ConnectionState.DISCONNECTED -> {
            Timber.i("$mTag doConnect: calling bluetooth connect api")
            mCoroutineScope.launch(Dispatchers.IO) {
                mBluetoothGatt = BluetoothApiManager.connect(this@doConnect)
            }
            DoConnectResult.RESULT_OK
        }
    }
}

internal fun BleDeviceImpl.doDisconnect(): DoDisconnectResult {
    Timber.i("$mTag doDisconnect: invoked currentState: ${connectionState.value}")
    if (pairingState.value != PairingState.NOT_PAIRED) {
        Timber.e("$mTag doDisconnect: invoked but device is not paired")
        return DoDisconnectResult.ERROR_NOT_PAIRED_DEVICE
    }
    return when (connectionState.value) {
        ConnectionState.CONNECTED -> {
            val mGattObject = mBluetoothGatt ?: let {
                Timber.e("$mTag doDisconnect: invoked but no gatt object")
                return DoDisconnectResult.ERROR_MISSING_GATT_OBJECT
            }
            Timber.i("$mTag doDisconnect: calling bluetooth disconnect api")
            mCoroutineScope.launch {
                BluetoothApiManager.disconnect(mGattObject)
            }
            DoDisconnectResult.RESULT_OK
        }
        ConnectionState.CONNECTING -> {
            Timber.e("$mTag doDisconnect: invoked but device is connecting")
            DoDisconnectResult.ERROR_GETTING_CONNECTED
        }
        ConnectionState.DISCONNECTING -> {
            Timber.e("$mTag doDisconnect: invoked but device is already disconnecting")
            DoDisconnectResult.ERROR_ALREADY_DISCONNECTING
        }
        ConnectionState.DISCONNECTED -> {
            Timber.e("$mTag doDisconnect: invoked but device is already disconnected")
            DoDisconnectResult.ERROR_ALREADY_DISCONNECTED
        }
    }
}

internal fun BleDeviceImpl.doOnConnectionStateChanged(newState: Int) {
    Timber.i("$mTag doOnConnectionStateChanged: invoked currentState: ${connectionState.value}")
    when (newState) {
        BluetoothProfile.STATE_CONNECTED -> ConnectionState.CONNECTED
        BluetoothProfile.STATE_CONNECTING -> ConnectionState.CONNECTING
        BluetoothProfile.STATE_DISCONNECTED -> ConnectionState.DISCONNECTED
        BluetoothProfile.STATE_DISCONNECTING -> ConnectionState.DISCONNECTING
        else -> null
    }?.let {
        Timber.i("$mTag doOnConnectionStateChanged: newState ? $it")
        mCoroutineScope.launch(Dispatchers.IO) { mConnectionState.emit(it) }
    } ?: let {
        Timber.e("$mTag doOnConnectionStateChanged: newState is unknown [$newState]")
    }
}