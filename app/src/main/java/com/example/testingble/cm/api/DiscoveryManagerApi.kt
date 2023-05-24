package com.example.testingble.cm.api

import kotlinx.coroutines.flow.Flow

interface DiscoveryManagerApi {
    fun startScan(): Flow<DiscoveredDevice>
}