package com.example.testingble.cm.sdk.discovery

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.example.testingble.cm.api.DisabledBluetoothAdapterException
import com.example.testingble.cm.sdk.DiscoveryManager
import com.example.testingble.cm.sdk.log.TimberTestTree
import com.example.testingble.cm.sdk.permission.PermissionManagerApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import timber.log.Timber

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DiscoveryManagerTest2 {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockBluetoothManager: BluetoothManager

    @Mock
    private lateinit var mockBluetoothAdapter: BluetoothAdapter

    @Before
    fun setup() {
        Timber.plant(TimberTestTree())
        MockitoAnnotations.openMocks(this)

        `when`(mockContext.getSystemService(Context.BLUETOOTH_SERVICE)).thenReturn(
            mockBluetoothManager
        )
        `when`(mockBluetoothManager.adapter).thenReturn(mockBluetoothAdapter)
        `when`(mockBluetoothAdapter.isEnabled).thenReturn(false)
    }

    /**
     * Here we will test, when the adapter is not enabled,
     * then startScan() will throw DisabledBluetoothAdapterException
     */
    @Test(expected = DisabledBluetoothAdapterException::class)
    fun testFunc(): Unit = runBlocking {
        val discoveryManager = DiscoveryManager(mockContext, PermissionManagerApi())
        discoveryManager.startScan().collect { }
    }

}