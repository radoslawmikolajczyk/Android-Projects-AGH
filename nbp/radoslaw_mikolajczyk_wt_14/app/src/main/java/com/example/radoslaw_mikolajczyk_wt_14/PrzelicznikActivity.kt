package com.example.radoslaw_mikolajczyk_wt_14

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.AdapterView.resolveSize
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal
import java.math.RoundingMode


class PrzelicznikActivity : AppCompatActivity() {

    private lateinit var countButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var inputNumber: EditText
    private lateinit var fromCurrency: Spinner
    private lateinit var toCurrency: Spinner
    private lateinit var errorTextView: TextView
    private lateinit var resTextView: TextView
    private var currenciesCode = HashMap<String, Double>()
    private var itemsToSpinner = ArrayList<String>()
    private var selectedFromCurrency = 0
    private var selectedToCurrency = 0
    private var plnArray = arrayListOf("PLN")
    private var itemsWithoutPln = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_przelicznik)
        assignCurrenciesCode()

        countButton = findViewById(R.id.countButtonId)
        resultTextView = findViewById(R.id.outputCounterId)
        inputNumber = findViewById(R.id.inputNumberId)
        fromCurrency = findViewById(R.id.fromCurrId)
        toCurrency = findViewById(R.id.toCurrId)
        errorTextView = findViewById(R.id.errorCounterId)
        resTextView = findViewById(R.id.resultCounterId)

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemsToSpinner)
        val plnAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, plnArray)
        val adapterWithoutPln: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemsWithoutPln)
        fromCurrency.adapter = adapter

        fromCurrency.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, view: View, position: Int, id: Long) {
                selectedFromCurrency = position
                if (fromCurrency.selectedItem.equals(plnArray[0])) {
                    toCurrency.adapter = adapterWithoutPln
                } else {
                    toCurrency.adapter = plnAdapter
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        toCurrency.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedToCurrency = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        countButton.setOnClickListener {
            val input = inputNumber.text.toString().toDoubleOrNull()

            if (input == null) {
                errorTextView.text = resources.getString(R.string.input_number_error)
            } else {
                errorTextView.text = resources.getString(R.string.empty_string)
                calculate(fromCurrency.selectedItem.toString(), toCurrency.selectedItem.toString(), input)
            }
        }
    }

    private fun assignCurrenciesCode() {
        val tablesData = StaticObject.currencyDataA + StaticObject.currencyDataB
        itemsToSpinner.add("PLN")
        for (i in tablesData) {
            currenciesCode[i.currencyName] = i.currency.toDouble()
            itemsToSpinner.add(i.currencyName)
            itemsWithoutPln.add(i.currencyName)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculate(from: String, to: String, value: Double) {
        val result: Double = if (from == "PLN") {
            value / currenciesCode[to]!!
        } else {
            value * currenciesCode[from]!!
        }
        val final = BigDecimal(result).setScale(4, RoundingMode.HALF_EVEN)
        resTextView.text = resources.getString(R.string.score_text)
        resultTextView.text = "$final $to"
    }
}