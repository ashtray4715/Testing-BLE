package com.example.testingble.cm.sdk.log

import timber.log.Timber

class TimberTestTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // we will do nothing here
    }
}