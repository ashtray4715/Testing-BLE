package com.example.testingble.discovery

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
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

class DiscoveryManager(private val context: Context) {
    @SuppressLint("MissingPermission")
    fun startScan(): Flow<DiscoveredDevice> = callbackFlow {
        Log.i(TAG, "startScan: flow started")

        val bluetoothService = context.getSystemService(Context.BLUETOOTH_SERVICE)
        val bleAdapter = (bluetoothService as BluetoothManager).adapter

        if (bleAdapter == null) {
            Log.e(TAG, "startScan: Bluetooth adapter is null")
            close(Exception("Bluetooth adapter is null"))
            return@callbackFlow
        }
        if (bleAdapter.isEnabled.not()) {
            Log.e(TAG, "startScan: Bluetooth adapter is not enabled")
            close(Exception("Bluetooth adapter is not enabled"))
            return@callbackFlow
        }

        val bleStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, intent: Intent?) {
                if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    when (intent.getIntExtra(EXTRA_STATE, ERROR)) {
                        BluetoothAdapter.STATE_OFF -> {
                            Log.e(TAG, "onReceive: Bluetooth status changed [OFF]")
                            close(Exception("Bluetooth adapter gets disabled"))
                        }
                        BluetoothAdapter.STATE_ON -> {
                            Log.e(TAG, "onReceive: Bluetooth status changed [ON]")
                        }
                    }
                }
            }
        }

        Log.i(TAG, "registerReceiver: invoked successfully")
        context.registerReceiver(bleStatusReceiver, IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        })

        val scanCallBack = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                Log.i(TAG, "onScanResult: address ? ${result.device.address}")
                trySend(DiscoveredDeviceImpl(result.device))
            }

            override fun onScanFailed(errorCode: Int) {
                Log.i(TAG, "onScanFailed: errorCode ? $errorCode")
                close(Exception("Scan failed with errorCode ? $errorCode"))
            }
        }

        Log.i(TAG, "startScan: invoked successfully")
        bleAdapter.bluetoothLeScanner.startScan(scanCallBack)

        awaitClose {
            Log.i(TAG, "stopScan: invoked successfully")
            bleAdapter.bluetoothLeScanner.stopScan(scanCallBack)
            Log.i(TAG, "unregisterReceiver: invoked successfully")
            context.unregisterReceiver(bleStatusReceiver)
        }
    }.flowOn(Dispatchers.Main)

    companion object {
        private const val TAG = "DiscoveryManager"
    }
}
