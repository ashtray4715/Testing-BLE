package com.example.testingble.cm.sdk

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import com.example.testingble.cm.api.BleDevice
import com.example.testingble.cm.api.DeviceManagerApi

internal class DeviceManager(private val context: Context) : DeviceManagerApi {

    override fun getDevice(address: String): BleDevice {
        val bluetoothService = context.getSystemService(Context.BLUETOOTH_SERVICE)
        val bleAdapter = (bluetoothService as BluetoothManager).adapter
        val mBluetoothDevice = bleAdapter.getRemoteDevice(address)
        return BleDeviceImpl(mBluetoothDevice)
    }

    @SuppressLint("MissingPermission")
    override fun getPairedDeviceList(): List<String> {
        val bluetoothService = context.getSystemService(Context.BLUETOOTH_SERVICE)
        val bleAdapter = (bluetoothService as BluetoothManager).adapter
        return bleAdapter?.bondedDevices?.map { it.address } ?: emptyList()
    }

//    private val deviceMap = mutableMapOf<String, BleDeviceImpl>()
//
//    override fun getDevice(address: String): BleDevice {
//        return deviceMap[address] ?: let {
//            val bluetoothService = context.getSystemService(Context.BLUETOOTH_SERVICE)
//            val bleAdapter = (bluetoothService as BluetoothManager).adapter
//            val mBluetoothDevice = bleAdapter.getRemoteDevice(address)
//            val currentDevice = BleDeviceImpl(mBluetoothDevice)
//            deviceMap[address] = currentDevice
//            currentDevice
//        }
//    }
//
//    override fun forgetDevice(address: String) {
//        if (deviceMap.containsKey(address)) {
//            deviceMap[address]?.runSafeDestroy()
//            deviceMap.remove(address)
//        }
//    }
}