package com.example.testingble.cm.testme

import android.os.Build

class Ok {

    fun getValue(): Int {
        return when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            true -> 1
            else -> 0
        }
    }

}