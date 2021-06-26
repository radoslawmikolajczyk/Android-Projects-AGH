package com.example.radoslaw_mikolajczyk_wt_14

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlin.collections.ArrayList


class GoldActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gold)
        setLineChartData()
    }

    @SuppressLint("SetTextI18n")
    private fun setLineChartData() {
        val dates = arrayListOf(StaticObject.goldData.keys)
        val xValues = dates[0]

        val rates = arrayListOf(StaticObject.goldData.values)
        val yValues = rates[0]

        val lineEntry = ArrayList<Entry>()
        for (i in 0 until xValues.size) {
            lineEntry.add(Entry(i.toFloat() + 1, yValues.elementAt(i).toFloat()))
        }

        val lineChart: LineChart = findViewById(R.id.goldLineChartId)
        lineChart.data =  LineData(LineDataSet(lineEntry, "Cena złota z ostatnich 30 dni"))
        lineChart.xAxis.isEnabled = false
        lineChart.description.isEnabled = false

        val actualGoldRate = findViewById<TextView>(R.id.actualGoldCourse)
        actualGoldRate.text = "Aktualny kurs złota: " + yValues.last() + " PLN"
    }
}