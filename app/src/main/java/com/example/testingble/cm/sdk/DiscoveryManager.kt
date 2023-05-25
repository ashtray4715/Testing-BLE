package com.example.testingble.cm.sdk

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.EXTRA_STATE
import android.bluetooth.BluetoothAdapter.ERROR
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.example.testingble.cm.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

internal class DiscoveryManager(private val context: Context) : DiscoveryManagerApi {
    @SuppressLint("MissingPermission")
    override fun startScan(): Flow<DiscoveredDevice> = callbackFlow {
        SdkLog.i("$TAG startScan: flow started")

        if (hasMissingPermissions()) {
            close(MissingPermissionsException())
            return@callbackFlow
        }

        val bluetoothService = context.getSystemService(Context.BLUETOOTH_SERVICE)
        val bleAdapter = (bluetoothService as BluetoothManager?)?.adapter

        if (bleAdapter == null) {
            SdkLog.e("$TAG startScan: Bluetooth adapter is null")
            close(NullBluetoothAdapterException())
            return@callbackFlow
        }
        if (bleAdapter.isEnabled.not()) {
            SdkLog.e("$TAG startScan: Bluetooth adapter is not enabled")
            close(DisabledBluetoothAdapterException())
            return@callbackFlow
        }

        val scanCallBack = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                SdkLog.i("$TAG onScanResult: address ? ${result.device.address}")
                try {
                    trySend(DiscoveredDeviceImpl(result.device))
                } catch (e: Exception) {
                    SdkLog.e("$TAG onScanResult: trySend failed with exception ${e.message}")
                    e.printStackTrace()
                }
            }

            override fun onScanFailed(errorCode: Int) {
                SdkLog.i("$TAG onScanFailed: errorCode ? $errorCode")
                close(ScanFailedException(errorCode = errorCode))
            }
        }

        SdkLog.i("$TAG startScan: invoked successfully")
        bleAdapter.bluetoothLeScanner.startScan(scanCallBack)

        val bleStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, intent: Intent?) {
                if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    when (intent.getIntExtra(EXTRA_STATE, ERROR)) {
                        BluetoothAdapter.STATE_OFF -> {
                            SdkLog.e("$TAG onReceive: Bluetooth status changed [OFF]")
                            close(DisabledBluetoothAdapterException())
                        }
                        BluetoothAdapter.STATE_ON -> {
                            SdkLog.e("$TAG onReceive: Bluetooth status changed [ON]")
                        }
                    }
                }
            }
        }

        SdkLog.i("$TAG registerReceiver: invoking")
        context.registerReceiver(bleStatusReceiver, IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        })

        awaitClose {
            SdkLog.i("$TAG stopScan: invoked successfully")
            bleAdapter.bluetoothLeScanner.stopScan(scanCallBack)
            SdkLog.i("$TAG unregisterReceiver: invoked successfully")
            context.unregisterReceiver(bleStatusReceiver)
        }
    }.flowOn(Dispatchers.IO)

    private fun hasMissingPermissions(): Boolean {
        return mutableListOf<Boolean>().apply {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    add(PermissionManager.hasBluetoothScanPermission(context))
                    add(PermissionManager.hasBluetoothConnectPermission(context))
                }
                else -> {
                    // todo - add permissions for below Android-S
                }
            }
        }.contains(false)
    }

    companion object {
        private const val TAG = "DiscoveryManager"
    }
}
