package com.example.radoslaw_mikolajczyk_wt_14

import android.content.Context
import android.location.LocationManager
import android.widget.TextView


class Validator(private val context: Context, private val errorMessage: TextView) {
    private val connection = InternetConnection(context)

    fun validate(cityName: String) : Boolean {
        return checkConnection() && checkInputCity(cityName)
    }

    fun validate() : Boolean {
        return checkConnection() && isGPSEnabled()
    }

    private fun checkInputCity(cityName: String) : Boolean{
        return if (cityName.matches(Regex(".*\\d.*")) || cityName.isEmpty()) {
            setErrorMessage(context.resources.getString(R.string.wrong_input_error))
            false
        } else {
            clearErrorMessage()
            true
        }
    }

    private fun checkConnection() : Boolean {
        return if (connection.isConnected()) {
            clearErrorMessage()
            true
        } else {
            setErrorMessage(context.resources.getString(R.string.no_network_error_message))
            false
        }
    }

    private fun setErrorMessage(error: String) {
        errorMessage.text = error
    }

    private fun clearErrorMessage() {
        errorMessage.text = context.resources.getString(R.string.empty)
    }

    private fun isGPSEnabled() : Boolean{
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return if (!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            errorMessage.text = context.resources.getString(R.string.launch_location_message)
            false
        } else {
            errorMessage.text = context.resources.getString(R.string.empty)
            true
        }
    }
}