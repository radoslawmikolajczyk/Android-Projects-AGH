package com.example.radoslaw_mikolajczyk_wt_14

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.*
import java.util.logging.Handler
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class RecyclerAdapter(val countryList: ArrayList<Record>, val context: Context) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private var connection = InternetConnection()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.kursy_rekord_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country: Record = countryList[position]
        holder.countryFlag.text = country.flag
        holder.currencyName.text = country.currencyName
        holder.currency.text = country.currency
        if (country.isBiggerCourse) {
            holder.arrow.setImageResource(R.drawable.green_up_icon)
        } else {
            holder.arrow.setImageResource(R.drawable.red_down_icon)
        }

        holder.itemView.setOnClickListener {
            if (connection.isConnected(context)) {
                StaticObject.currentDataRecord = createDataRecord(holder.currencyName.text.toString())
                val table = StaticObject.currentDataRecord.table
                val code = StaticObject.currentDataRecord.currencyCode
                makeRequest("http://api.nbp.pl/api/exchangerates/rates/$table/$code/last/30/?format=json")
                Timer().schedule(800) {
                    val switchActivityIntent = Intent(context, DetailsActivity::class.java)
                    context.startActivity(switchActivityIntent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countryFlag = itemView.findViewById(R.id.panstwoId) as TextView
        val currencyName = itemView.findViewById(R.id.nazwaWalutyId) as TextView
        val currency = itemView.findViewById(R.id.kursWartoscId) as TextView
        val arrow = itemView.findViewById(R.id.arrowUpDownId) as ImageView
    }

    private fun createDataRecord(currCode: String) : DataRecord {
        var table = ""
        for (i in StaticObject.currencyDataA) {
            if(currCode == i.currencyName) {
                table = "a"
                break
            }
        }
        if (table == "") {
            table = "b"
        }
        return DataRecord(currCode, table)
    }

    private fun makeRequest(url: String) {
        val queue = Volley.newRequestQueue(context)

        val currenciesRatesRequest = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    loadData(response)
                }, { error -> println(error.toString()) })
        queue.add(currenciesRatesRequest)
    }

    private fun loadData(response: JSONObject) {
        response.let {
            val rates = response.getJSONArray("rates")
            val arr = ArrayList<Double>()
            for (i in 0 until rates.length()) {
                arr.add(rates.getJSONObject(i).getDouble("mid"))
            }

            StaticObject.currentRatesArray = arr
        }
    }

}