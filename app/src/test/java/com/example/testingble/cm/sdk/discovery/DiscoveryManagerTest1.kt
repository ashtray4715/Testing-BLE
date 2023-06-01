package com.example.testingble.cm.sdk.discovery

import android.content.Context
import com.example.testingble.cm.api.NullBluetoothAdapterException
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
class DiscoveryManagerTest1 {

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        Timber.plant(TimberTestTree())
        MockitoAnnotations.openMocks(this)
        `when`(mockContext.getSystemService(Context.BLUETOOTH_SERVICE)).thenReturn(null)
    }

    /**
     * Here we will test when there is no bluetooth support in the phone
     * then start scan will normally throw NullBluetoothAdapterException
     */
    @Test(expected = NullBluetoothAdapterException::class)
    fun testFunc(): Unit = runBlocking {
        val discoveryManager = DiscoveryManager(mockContext, PermissionManagerApi())
        discoveryManager.startScan().collect {}
    }
}