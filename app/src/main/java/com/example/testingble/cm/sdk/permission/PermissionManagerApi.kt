package com.example.testingble.cm.sdk.permission

import android.content.Context

open class PermissionManagerApi {
    open fun hasBluetoothScanPermission(context: Context): Boolean {
        return true
    }

    open fun hasBluetoothConnectPermission(context: Context): Boolean {
        return true
    }
}