package com.example.radoslaw_mikolajczyk_wt_14

import java.util.*
import kotlin.collections.ArrayList

object StaticObject {
    var currencyDataA = ArrayList<Record>()
    var currencyDataB = ArrayList<Record>()
    var goldData = TreeMap<String, Double>()
    lateinit var currentDataRecord: DataRecord
    var currentRatesArray = ArrayList<Double>()
}