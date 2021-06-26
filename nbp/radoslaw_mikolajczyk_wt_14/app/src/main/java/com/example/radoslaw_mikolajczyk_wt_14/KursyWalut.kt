package com.example.radoslaw_mikolajczyk_wt_14

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class KursyWalut : AppCompatActivity() {

    private lateinit var recycler: RecyclerView

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kursy_walut)

        recycler = findViewById(R.id.kursyRecyclerViewId)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        val recordArray = StaticObject.currencyDataA + StaticObject.currencyDataB
        val adapter = RecyclerAdapter(recordArray as ArrayList<Record>, this)
        recycler.adapter = adapter
    }
}