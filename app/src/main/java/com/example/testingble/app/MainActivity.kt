package com.example.testingble.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.testingble.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.text_view)

        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                    val deviceList = mutableSetOf<String>()
                    BleApp.getSdk().getDiscoveryManager().startScan().onStart {
                        deviceList.clear()
                        textView.append("flow started successfully\n")
                    }.onCompletion {
                        deviceList.clear()
                        textView.append("flow completed successfully\n")
                    }.collect {
                        if (deviceList.contains(it.address).not()) {
                            deviceList.add(it.address)
                            AppLog.i("$TAG found a device ${it.address}")
                            textView.append("found a device ${it.address}\n")
                        }
                    }
                } catch (e: Exception) {
                    AppLog.e("$TAG exception found in start scan flow [${e.message}]")
                    textView.append("exception found in start scan flow [${e.message}]\n")
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        BleApp.getSdk().getDeviceManager().getPairedDeviceList().apply {
            AppLog.i("$TAG onStart: paired device list size $size")
        }.forEach {
            AppLog.i("$TAG onStart: paired device id $it")
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}