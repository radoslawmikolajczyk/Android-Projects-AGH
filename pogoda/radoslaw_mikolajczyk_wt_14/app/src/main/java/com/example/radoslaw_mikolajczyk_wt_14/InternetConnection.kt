package com.example.radoslaw_mikolajczyk_wt_14

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class InternetConnection(private val context: Context) {

    fun isConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}