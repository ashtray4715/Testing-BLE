package com.example.testingble.cm.sdk.discovery

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.IntentFilter
import com.example.testingble.cm.sdk.DiscoveryManager
import com.example.testingble.cm.sdk.IntentFilterFactory
import com.example.testingble.cm.sdk.log.TimberTestTree
import com.example.testingble.cm.sdk.permission.PermissionManagerApi
import io.mockk.*
import kotlinx.coroutines.*
import org.junit.Before
import org.junit.Test
import timber.log.Timber

class DiscoveryManagerTest3 {

    @Before
    fun setup() {
        Timber.plant(TimberTestTree())
    }

    /**
     * Here we will test, startScan() emits 2 devices,
     * we will collect these values and perform testing
     */
    @Test
    fun testFunc(): Unit = runBlocking {
        val mContext = mockk<Context>()
        val mBluetoothManager = mockk<BluetoothManager>()
        val mBluetoothAdapter = mockk<BluetoothAdapter>()
        val mBluetoothLeScanner = mockk<BluetoothLeScanner>()
        val mScanCallback = slot<ScanCallback>()

        val mIntentFilterFactory = mockk<IntentFilterFactory>()
        val mIntentFilter = mockk<IntentFilter>()

        val scanResult1 = mockk<ScanResult>().apply {
            every { device.address } returns "1234567890"
        }
        val scanResult2 = mockk<ScanResult>().apply {
            every { device.address } returns "9876543210"
        }

        every { mContext.getSystemService(Context.BLUETOOTH_SERVICE) } returns mBluetoothManager
        every { mBluetoothManager.adapter } returns mBluetoothAdapter
        every { mBluetoothAdapter.bluetoothLeScanner } returns mBluetoothLeScanner
        every { mBluetoothAdapter.isEnabled } returns true
        every { mIntentFilterFactory.getNewIntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED) } returns mIntentFilter

        every { mBluetoothLeScanner.startScan(capture(mScanCallback)) } answers {
            mScanCallback.captured.onScanResult(1, scanResult1)
            mScanCallback.captured.onScanResult(2, scanResult2)
        }
        every { mBluetoothLeScanner.stopScan(capture(mScanCallback)) } returns Unit
        every { mContext.registerReceiver(any(), any()) } returns mockk()
        every { mContext.unregisterReceiver(any()) } returns Unit

        val myClass = DiscoveryManager(mContext, PermissionManagerApi())
        DiscoveryManager::class.java.getDeclaredField("intentFilterFactory").let {
            it.isAccessible = true
            it.set(myClass, mIntentFilterFactory)
        }

        val listOfAddresses = listOf("1234567890", "9876543210")
        val flow = myClass.startScan()

        val mOutputList = mutableListOf<String>()
        var mExceptionFound = false

        val mJob = launch {
            try {
                flow.collect {
                    assert(listOfAddresses.contains(it.address))
                    mOutputList.add(it.address)
                }
            } catch (e: CancellationException) {
                mExceptionFound = false
            } catch (e: Exception) {
                e.printStackTrace()
                mExceptionFound = true
            }
        }



        delay(200)
        mJob.cancel()

        assert(!mExceptionFound)
        assert(mOutputList.size == 2)

        verify(exactly = 1) { mBluetoothLeScanner.startScan(any()) }
        verify(exactly = 1) { mBluetoothLeScanner.stopScan(any<ScanCallback>()) }
        verify(exactly = 1) { mContext.registerReceiver(any(), any()) }
        verify(exactly = 1) { mContext.unregisterReceiver(any()) }
    }

}