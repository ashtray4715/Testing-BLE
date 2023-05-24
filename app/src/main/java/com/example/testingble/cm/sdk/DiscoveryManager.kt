package com.example.testingble.cm.sdk

import android.Manifest
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
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.testingble.cm.api.DiscoveredDevice
import com.example.testingble.cm.api.DiscoveryManagerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

internal class DiscoveryManager(private val context: Context) : DiscoveryManagerApi {
    @SuppressLint("MissingPermission")
    override fun startScan(): Flow<DiscoveredDevice> = callbackFlow {
        Log.i(TAG, "startScan: flow started")

        getMissingPermissions().let { permissions ->
            if (permissions.isNotEmpty()) {
                close(Exception("Missing permissions ${permissions.joinToString(", ")}"))
                return@callbackFlow
            }
        }

        val bluetoothService = context.getSystemService(Context.BLUETOOTH_SERVICE)
        val bleAdapter = (bluetoothService as BluetoothManager?)?.adapter

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

        val scanCallBack = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                Log.i(TAG, "onScanResult: address ? ${result.device.address}")
                try {
                    trySend(DiscoveredDeviceImpl(result.device))
                } catch (e: Exception) {
                    Log.e(TAG, "onScanResult: trySend failed with exception ${e.message}")
                    e.printStackTrace()
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.i(TAG, "onScanFailed: errorCode ? $errorCode")
                close(Exception("Scan failed with errorCode ? $errorCode"))
            }
        }

        Log.i(TAG, "startScan: invoked successfully")
        bleAdapter.bluetoothLeScanner.startScan(scanCallBack)

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

        Log.i(TAG, "registerReceiver: invoking")
        context.registerReceiver(bleStatusReceiver, IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        })

        awaitClose {
            Log.i(TAG, "stopScan: invoked successfully")
            bleAdapter.bluetoothLeScanner.stopScan(scanCallBack)
            Log.i(TAG, "unregisterReceiver: invoked successfully")
            context.unregisterReceiver(bleStatusReceiver)
        }
    }.flowOn(Dispatchers.Main)

    private fun getMissingPermissions(): List<String> {
        val permissionList = mutableListOf<String>().apply {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    add(Manifest.permission.BLUETOOTH_SCAN)
                    add(Manifest.permission.BLUETOOTH_CONNECT)
                }
                else -> {
                    // todo - add permissions for below Android-S
                }
            }
        }
        return permissionList.mapNotNull {
            val pStatus = ContextCompat.checkSelfPermission(context, it)
            if (pStatus == PackageManager.PERMISSION_GRANTED) null else it
        }
    }

    companion object {
        private const val TAG = "DiscoveryManager"
    }
}
