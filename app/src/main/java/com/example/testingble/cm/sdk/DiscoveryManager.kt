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
import com.example.testingble.cm.sdk.permission.PermissionManagerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

internal class DiscoveryManager(
    private val context: Context,
    private val permissionManager: PermissionManagerApi
) : DiscoveryManagerApi {

    private val intentFilterFactory = IntentFilterFactory()

    @SuppressLint("MissingPermission")
    override fun startScan(): Flow<DiscoveredDevice> = callbackFlow {
        Timber.i("$TAG startScan: flow started")

        if (hasMissingPermissions()) {
            close(MissingPermissionsException())
            return@callbackFlow
        }

        val bluetoothService = context.getSystemService(Context.BLUETOOTH_SERVICE)
        val bleAdapter = (bluetoothService as BluetoothManager?)?.adapter

        if (bleAdapter == null) {
            Timber.e("$TAG startScan: Bluetooth adapter is null")
            close(NullBluetoothAdapterException())
            return@callbackFlow
        }
        if (bleAdapter.isEnabled.not()) {
            Timber.e("$TAG startScan: Bluetooth adapter is not enabled")
            close(DisabledBluetoothAdapterException())
            return@callbackFlow
        }

        val bleStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, intent: Intent?) {
                if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    when (intent.getIntExtra(EXTRA_STATE, ERROR)) {
                        BluetoothAdapter.STATE_OFF -> {
                            Timber.e("$TAG onReceive: Bluetooth status changed [OFF]")
                            close(DisabledBluetoothAdapterException())
                        }
                        BluetoothAdapter.STATE_ON -> {
                            Timber.e("$TAG onReceive: Bluetooth status changed [ON]")
                        }
                    }
                }
            }
        }

        val intentFilter = intentFilterFactory.getNewIntentFilter(
            action = BluetoothAdapter.ACTION_STATE_CHANGED
        )

        Timber.i("$TAG registerReceiver: invoking")
        context.registerReceiver(bleStatusReceiver, intentFilter)

        val scanCallBack = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                Timber.i("$TAG onScanResult: address ? ${result.device.address}")
                try {
                    trySend(DiscoveredDeviceImpl(result.device))
                } catch (e: Exception) {
                    Timber.e("$TAG onScanResult: trySend failed with exception ${e.message}")
                    e.printStackTrace()
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Timber.i("$TAG onScanFailed: errorCode ? $errorCode")
                close(ScanFailedException(errorCode = errorCode))
            }
        }

        Timber.i("$TAG startScan: invoked successfully")
        bleAdapter.bluetoothLeScanner.startScan(scanCallBack)

        awaitClose {
            Timber.i("$TAG stopScan: invoked successfully")
            bleAdapter.bluetoothLeScanner.stopScan(scanCallBack)
            Timber.i("$TAG unregisterReceiver: invoked successfully")
            context.unregisterReceiver(bleStatusReceiver)
        }
    }.flowOn(Dispatchers.IO)

    private fun hasMissingPermissions(): Boolean {
        return mutableListOf<Boolean>().apply {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    add(permissionManager.hasBluetoothScanPermission(context))
                    add(permissionManager.hasBluetoothConnectPermission(context))
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
