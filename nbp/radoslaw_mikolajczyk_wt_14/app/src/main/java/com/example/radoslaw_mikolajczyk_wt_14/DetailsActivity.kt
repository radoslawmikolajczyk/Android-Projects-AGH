package com.example.radoslaw_mikolajczyk_wt_14

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.math.BigDecimal
import java.math.RoundingMode

class DetailsActivity : AppCompatActivity() {

    private lateinit var currencyCode: TextView
    private lateinit var todayCourseTextView: TextView
    private lateinit var yesterdayCourseTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        currencyCode = findViewById(R.id.currencyCodeChartId)
        todayCourseTextView = findViewById(R.id.todayCurrDetailsId)
        yesterdayCourseTextView = findViewById(R.id.yesterdayCurrDetailId)

        setLineChartData()
    }

    @SuppressLint("SetTextI18n")
    private fun setLineChartData() {
        val monthlyValues = StaticObject.currentRatesArray
        val weeklyValues = monthlyValues.subList(monthlyValues.size-7, monthlyValues.size)
        val currentCourse = BigDecimal(monthlyValues.last()).setScale(7, RoundingMode.HALF_EVEN)
        val yesterdayCourse = BigDecimal(monthlyValues[monthlyValues.size-2]).setScale(7, RoundingMode.HALF_EVEN)
        val currCode = StaticObject.currentDataRecord.currencyCode

        val weeklyLineEntry = ArrayList<Entry>()
        for (i in 0 until weeklyValues.size) {
            weeklyLineEntry.add(Entry(i.toFloat() + 1, weeklyValues.elementAt(i).toFloat()))
        }
        val weeklyLineChart: LineChart = findViewById(R.id.weekRatesChartId)
        weeklyLineChart.data = LineData(LineDataSet(weeklyLineEntry, "Kurs $currCode z ostatniego tygodnia"))
        weeklyLineChart.xAxis.isEnabled = false
        weeklyLineChart.description.isEnabled = false

        val monthlyLineEntry = ArrayList<Entry>()
        for (i in 0 until monthlyValues.size) {
            monthlyLineEntry.add(Entry(i.toFloat() + 1, monthlyValues.elementAt(i).toFloat()))
        }
        val monthlyLineChart: LineChart = findViewById(R.id.monthRatesChartId)
        monthlyLineChart.data = LineData(LineDataSet(monthlyLineEntry, "Kurs $currCode z ostatniego miesiąca"))
        monthlyLineChart.xAxis.isEnabled = false
        monthlyLineChart.description.isEnabled = false

        todayCourseTextView.text = "Bieżący kurs: $currentCourse PLN"
        yesterdayCourseTextView.text = "Wczorajszy kurs: $yesterdayCourse PLN"
        currencyCode.text = currCode
    }
}