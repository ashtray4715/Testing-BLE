package com.example.testingble.app

import android.util.Log

object AppLog {
    private const val TAG = "[MG_AppLog]"

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