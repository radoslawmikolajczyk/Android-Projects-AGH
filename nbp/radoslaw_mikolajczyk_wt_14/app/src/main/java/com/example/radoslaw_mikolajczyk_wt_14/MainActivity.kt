package com.example.radoslaw_mikolajczyk_wt_14

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import org.json.JSONArray
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Imię i nazwisko: Radosław Mikołajczyk
 *
 * Zrealizowałem każdy z podpunktów. Obsługuje brak połączenia sieciowego oraz parę innych błędów,
 * np nieprawidłowy input liczby w przeliczniku walut (np. użytkownik wpisuje znak '.', wprowadzenie
 * liczby .090 powoduje ze liczba jest rzutowana na 0.090).
 *
 * Flagi sa uzyskane za pomoca emoji, w miejscach gdzie dana waluta nie ma swojej flagi, jest biała
 * flaga z pytajnikiem
 */

class MainActivity : AppCompatActivity() {
    private lateinit var kursyWalutButton: Button
    private lateinit var zlotoButton: Button
    private lateinit var przelicznikButton: Button
    private lateinit var connectionError: TextView
    private var connection = InternetConnection()
    private val flagOffset = 0x1F1E6
    private val asciiOffset = 0x41

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        kursyWalutButton = findViewById(R.id.kursyWalutOdnosnikId)
        zlotoButton = findViewById(R.id.zlotoOdnosnikId)
        przelicznikButton = findViewById(R.id.przelicznikOdnosnikId)
        connectionError = findViewById(R.id.errorConnectionMainId)

        if (connection.isConnected(this)) {
            getDataFromNbp()
        }

        kursyWalutButton.setOnClickListener {
            if (connection.isConnected(this)) {
                getDataFromNbp()
                setErrorMessage(false)
                val switchActivityIntent = Intent(this, KursyWalut::class.java)
                startActivity(switchActivityIntent)
            } else {
                setErrorMessage(true)
            }
        }

        zlotoButton.setOnClickListener {
            if (connection.isConnected(this)) {
                getDataFromNbp()
                setErrorMessage(false)
                val switchActivityIntent = Intent(this, GoldActivity::class.java)
                startActivity(switchActivityIntent)
            } else {
                setErrorMessage(true)
            }
        }

        przelicznikButton.setOnClickListener {
            if (connection.isConnected(this)) {
                getDataFromNbp()
                setErrorMessage(false)
                val switchActivityIntent = Intent(this, PrzelicznikActivity::class.java)
                startActivity(switchActivityIntent)
            } else {
                setErrorMessage(true)
            }
        }
    }

    private fun setErrorMessage(exp: Boolean) {
        if(exp) {
            connectionError.text = resources.getString(R.string.connection_error_msg)
        } else {
            connectionError.clearComposingText()
        }
    }

    private fun makeRequest(url: String, reqAim: String) {
        val queue = newRequestQueue(this)

        val currenciesRatesRequest = JsonArrayRequest(Request.Method.GET, url, null,
                { response ->
                    loadData(response, reqAim)
            }, { error -> println(error.toString()) })
        queue.add(currenciesRatesRequest)
    }

    private fun loadData(response: JSONArray?, reqAim: String) {
        when (reqAim) {
            "tableA" -> loadTableData(response, "A")
            "tableB" -> loadTableData(response, "B")
            "gold" -> loadGoldData(response)
        }
    }

    private fun loadGoldData(response: JSONArray?) {
        response?.let {
            val goldDataLength = response.length()
            val goldRates = TreeMap<String, Double>()
            for (i in 0 until goldDataLength) {
                val date = response.getJSONObject(i).getString("data")
                val rate = response.getJSONObject(i).getDouble("cena")

                goldRates[date] = rate
            }
            StaticObject.goldData = goldRates
        }
    }

    private fun loadTableData(response: JSONArray?, table: String) {
        response?.let {
            val actualRatesMap = HashMap<String, BigDecimal>()
            val actualRates = response.getJSONObject(1).getJSONArray("rates")
            val ratesCount = actualRates.length()
            for (i in 0 until ratesCount) {
                val currencyCode = actualRates.getJSONObject(i).getString("code")
                val currencyRate = actualRates.getJSONObject(i).getDouble("mid")
                val finalCurrencyRate = BigDecimal(currencyRate).setScale(7, RoundingMode.HALF_EVEN)
                actualRatesMap[currencyCode] = finalCurrencyRate
            }

            val oldRatesMap = HashMap<String, BigDecimal>()
            val oldRates = response.getJSONObject(0).getJSONArray("rates")
            val oldRatesCount = oldRates.length()
            for (i in 0 until oldRatesCount) {
                val currencyCode = oldRates.getJSONObject(i).getString("code")
                val currencyRate = oldRates.getJSONObject(i).getDouble("mid")
                val finalCurrencyRate = BigDecimal(currencyRate).setScale(4, RoundingMode.HALF_EVEN)
                oldRatesMap[currencyCode] = finalCurrencyRate
            }

            val recordList = ArrayList<Record>()

            for ((k, v) in actualRatesMap) {
                var isBiggerCurr = true
                if (oldRatesMap[k]!! > v) {
                    isBiggerCurr = false
                }
                recordList.add(Record(convertToFlag(k.substring(0,3)),k,isBiggerCurr,v.toString()))
            }

            when (table) {
                "A" -> StaticObject.currencyDataA = recordList
                "B" -> StaticObject.currencyDataB = recordList
            }
        }
    }

    private fun convertToFlag(countryCode: String) : String{
        val firstChar = Character.codePointAt(countryCode, 0) - asciiOffset + flagOffset
        val secondChar = Character.codePointAt(countryCode, 1) - asciiOffset + flagOffset

        return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
    }

    private fun getDataFromNbp() {
        makeRequest("http://api.nbp.pl/api/exchangerates/tables/a/last/2/?format=json", "tableA")
        makeRequest("http://api.nbp.pl/api/exchangerates/tables/b/last/2/?format=json", "tableB")
        makeRequest("http://api.nbp.pl/api/cenyzlota/last/30/?format=json", "gold")
    }
}