package com.example.testingble.cm.sdk.discovery

import android.content.Context
import com.example.testingble.cm.api.NullBluetoothAdapterException
import com.example.testingble.cm.sdk.DiscoveryManager
import com.example.testingble.cm.sdk.log.TimberTestTree
import com.example.testingble.cm.sdk.permission.PermissionManagerApi
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import timber.log.Timber

class DiscoveryManagerTest1 {

    @Before
    fun setup() {
        Timber.plant(TimberTestTree())
    }

    /**
     * Here we will test when there is no bluetooth support in the phone
     * then start scan will normally throw NullBluetoothAdapterException
     */
    @Test(expected = NullBluetoothAdapterException::class)
    fun testFunc(): Unit = runBlocking {
        val mContext = mockk<Context>()
        every { mContext.getSystemService(Context.BLUETOOTH_SERVICE) } returns null

        val discoveryManager = DiscoveryManager(mContext, PermissionManagerApi())
        discoveryManager.startScan().collect {}
    }
}