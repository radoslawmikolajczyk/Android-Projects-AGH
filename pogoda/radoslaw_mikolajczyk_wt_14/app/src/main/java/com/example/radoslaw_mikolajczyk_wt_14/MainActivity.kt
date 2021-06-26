package com.example.radoslaw_mikolajczyk_wt_14


import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var apiKey: String
    private lateinit var cloudInput: TextView
    private lateinit var windInput: TextView
    private lateinit var humidityInput: TextView
    private lateinit var sunriseInput: TextView
    private lateinit var pressureInput: TextView
    private lateinit var sunsetInput: TextView
    private lateinit var cityInput: EditText
    private lateinit var actualDateInput: TextView
    private lateinit var cityNameDisplay: TextView
    private lateinit var temperatureInput: TextView
    private lateinit var errorMessage: TextView
    private lateinit var searchButton: Button
    private lateinit var localizationButton: Button
    private lateinit var spinner: ProgressBar
    private lateinit var validator: Validator
    private lateinit var weather: Weather
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var isLocateButtonClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getComponents()
        addActionListeners()
    }

    private fun getComponents() {
        cloudInput = findViewById(R.id.cloudInputId)
        windInput = findViewById(R.id.windInputId)
        humidityInput = findViewById(R.id.humidityInputId)
        sunriseInput = findViewById(R.id.sunriseInputId)
        pressureInput = findViewById(R.id.pressureInputId)
        sunsetInput = findViewById(R.id.sunsetInputId)
        cityInput = findViewById(R.id.cityInputTextId)
        actualDateInput = findViewById(R.id.actualDateValueId)
        cityNameDisplay = findViewById(R.id.cityNameId)
        temperatureInput = findViewById(R.id.tempValueId)
        errorMessage = findViewById(R.id.errorMessageId)
        searchButton = findViewById(R.id.searchButtonId)
        localizationButton = findViewById(R.id.localizationButtonId)
        spinner = findViewById(R.id.spinner)
        spinner.visibility = View.GONE
        validator = Validator(this, errorMessage)
        apiKey = resources.getString(R.string.api_key)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setLocationCallback()
    }

    private fun setLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                if (isLocateButtonClicked) {
                    p0 ?: return
                    for (location in p0.locations) {
                        getDataByLocalization(location.latitude, location.longitude)
                        break
                    }
                    isLocateButtonClicked = false
                }
            }
        }
    }

    private fun addActionListeners() {

        cityInput.addTextChangedListener {
            errorMessage.text = resources.getString(R.string.empty)
        }

        searchButton.setOnClickListener {
            errorMessage.text = resources.getString(R.string.empty)
            if(validator.validate(cityInput.text.toString())) {
                spinner.visibility = View.VISIBLE
                getDataByCityName(cityInput.text.toString())
                hideKeyboard()
                cityInput.setText("")
                cityInput.clearFocus()
            }
        }

        localizationButton.setOnClickListener {
            hideKeyboard()
            cityInput.setText("")
            cityInput.clearFocus()
            errorMessage.text = resources.getString(R.string.empty)
            if(validator.validate()) {
                isLocateButtonClicked = true
                spinner.visibility = View.VISIBLE
                locate()
            }
        }
    }

    private fun checkGpsPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            errorMessage.text = resources.getString(R.string.grant_permission_and_click_again)
            spinner.visibility = View.GONE
        }
    }

    private fun locate() {
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        checkGpsPermission()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun Activity.hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(findViewById<View>(android.R.id.content).windowToken, 0);
    }

    private fun refresh(wet: Weather) {
        cityNameDisplay.text = wet.cityName
        temperatureInput.text = wet.temp
        actualDateInput.text = wet.actualDate
        cloudInput.text = wet.cloud
        windInput.text = wet.wind
        humidityInput.text = wet.humidity
        sunriseInput.text = wet.sunrise
        pressureInput.text = wet.pressure
        sunsetInput.text = wet.sunset
    }

    private fun getDataByCityName(cityName: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=$apiKey"
        val weatherRequest = JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                { response ->
                    loadData(response)
                    spinner.visibility = View.GONE
                }, {
            spinner.visibility = View.GONE
            errorMessage.text = resources.getString(R.string.city_not_found)
        })
        queue.add(weatherRequest)
    }

    private fun getDataByLocalization(lat: Double, lon: Double) {
        errorMessage.text = resources.getString(R.string.empty)
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$apiKey"
        val weatherRequest = JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                { response ->
                    loadData(response)
                    spinner.visibility = View.GONE
                }, {
            spinner.visibility = View.GONE
            errorMessage.text = resources.getString(R.string.location_error)
        })
        queue.add(weatherRequest)
    }

    private fun loadData(response: JSONObject?){
        response?.let {
            val main = response.getJSONObject("main")
            val sys = response.getJSONObject("sys")
            val wind = response.getJSONObject("wind")
            val cloud = response.getJSONObject("clouds")
            val updateAt: Long = response.getLong("dt")
            var cityName = response.getString("name")
            val temp = main.getDouble("temp")
            val clouds = cloud.getString("all") + " %"
            val windSpeed = wind.getDouble("speed")
            val humidity = main.getString("humidity") + " %"
            val sunrise: Long = sys.getLong("sunrise")
            val pressure = main.getString("pressure") + " hPa"
            val sunset: Long = sys.getLong("sunset")

            val updatedAtText = "Zaktualizowano: " + SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(Date(updateAt * 1000))
            val celsiusTemp = BigDecimal(temp - 273.15).setScale(1, RoundingMode.HALF_EVEN).toString() + "°C"
            val windSpeedKmH = BigDecimal(windSpeed * 3600 / 1000).setScale(1, RoundingMode.HALF_EVEN).toString() + " km/h"
            val sunriseText = SimpleDateFormat("HH:mm", Locale.US).format(sunrise * 1000)
            val sunsetText = SimpleDateFormat("HH:mm", Locale.US).format(sunset * 1000)

            if (cityName.isEmpty()) {
                cityName = "Twoja lokalizacja"
            }

            weather =  Weather(cityName, celsiusTemp, updatedAtText, clouds, windSpeedKmH, humidity, sunriseText, pressure, sunsetText)
            refresh(weather)
        }
    }
}