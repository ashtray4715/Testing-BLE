package com.example.testingble.cm.sdk

import android.util.Log

object SdkLog {
    private const val TAG = "[MG_SDKLog]"

    fun i(message: String) {
        Log.i(TAG, message)
    }

    fun e(message: String) {
        Log.e(TAG, message)
    }

    fun w(message: String) {
        Log.w(TAG, message)
    }

    fun d(message: String) {
        Log.d(TAG, message)
    }
}