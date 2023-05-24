package com.example.testingble

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.text_view)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                    val deviceList = mutableSetOf<String>()
                    BleApp.getSdk().discoveryManagerApi.startScan().onStart {
                        deviceList.clear()
                        textView.append("flow started successfully\n")
                    }.onCompletion {
                        deviceList.clear()
                        textView.append("flow completed successfully\n")
                    }.collect {
                        if (deviceList.contains(it.address).not()) {
                            deviceList.add(it.address)
                            Log.i(TAG, "found a device ${it.address}")
                            textView.append("found a device ${it.address}\n")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "exception found in start scan flow [${e.message}]")
                    textView.append("exception found in start scan flow [${e.message}]\n")
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        BleApp.getSdk().deviceManagerApi.getPairedDeviceList().apply {
            Log.i(TAG, "onStart: paired device list size $size")
        }.forEach {
            Log.i(TAG, "onStart: paired device id $it")
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}