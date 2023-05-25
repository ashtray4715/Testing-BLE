package com.example.testingble.cm.api

class ModuleNotInitializedException : Exception("Module not initialized")

class MissingPermissionsException : Exception("Some permissions are missing")

class NullBluetoothAdapterException : Exception("Bluetooth adapter is null")

class DisabledBluetoothAdapterException : Exception("Bluetooth adapter is not enabled")

class ScanFailedException(errorCode: Int) : Exception("Scan failed with errorCode ? $errorCode")