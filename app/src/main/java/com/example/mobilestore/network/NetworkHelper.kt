package com.example.mobilestore.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkHelper(private val context: Context) {
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager // ← отступ 12 пробелов (4+8)
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network)
            ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
