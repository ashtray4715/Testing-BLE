package com.example.testingble.cm.sdk.discovery

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.example.testingble.cm.api.DisabledBluetoothAdapterException
import com.example.testingble.cm.sdk.DiscoveryManager
import com.example.testingble.cm.sdk.log.TimberTestTree
import com.example.testingble.cm.sdk.permission.PermissionManagerApi
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import timber.log.Timber

class DiscoveryManagerTest2 {

    @Before
    fun setup() {
        Timber.plant(TimberTestTree())
    }

    /**
     * Here we will test, when the adapter is not enabled,
     * then startScan() will throw DisabledBluetoothAdapterException
     */
    @Test(expected = DisabledBluetoothAdapterException::class)
    fun testFunc(): Unit = runBlocking {

        val mContext = mockk<Context>()
        val mBluetoothManager = mockk<BluetoothManager>()
        val mBluetoothAdapter = mockk<BluetoothAdapter>()

        every { mContext.getSystemService(Context.BLUETOOTH_SERVICE) } returns mBluetoothManager
        every { mBluetoothManager.adapter } returns mBluetoothAdapter
        every { mBluetoothAdapter.isEnabled } returns false

        val discoveryManager = DiscoveryManager(mContext, PermissionManagerApi())
        discoveryManager.startScan().collect { }
    }

}