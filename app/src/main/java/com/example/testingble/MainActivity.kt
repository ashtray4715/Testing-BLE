package com.example.testingble

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.testingble.discovery.DiscoveryManager
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var discoveryManager: DiscoveryManager
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.text_view)
        discoveryManager = DiscoveryManager(applicationContext)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                    discoveryManager.startScan().onStart {
                        textView.append("flow started successfully\n")
                    }.onCompletion {
                        textView.append("flow completed successfully\n")
                    }.collect {
                        Log.i(TAG, "found a device ${it.address}")
                        textView.append("found a device ${it.address}\n")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "exception found in start scan flow [${e.message}]")
                    textView.append("exception found in start scan flow [${e.message}]\n")
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}