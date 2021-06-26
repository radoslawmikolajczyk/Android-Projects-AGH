package com.example.radoslaw_mikolajczyk_wt_14

import kotlin.math.sqrt


class Operations: Calculations {

    private var result: Double = 0.0

    override fun addNumber(num: Double) {
        this.result += num
    }

    override fun subNumber(num: Double) {
        this.result -= num
    }

    override fun mulNumber(num: Double) {
        this.result *= num
    }

    override fun divNumber(num: Double) {
        if (num != 0.0) {
            this.result /= num
        } else {
            this.result = 0.0
        }
    }

    override fun percentNumber(num: Double) {
        if (this.result == 0.0) {
            this.result = num/100
        } else {
            this.result *= num / 100
        }
    }

    override fun sqrtNumber(num: Double) {
        this.result = sqrt(num)
    }

    override fun clear() {
        this.result = 0.0
    }

    override fun changeSign() {
        if (this.result != 0.0) {
            this.result = -this.result
        }
    }

    fun getResult(): Double {
        return this.result
    }

    fun setResult(res: Double) {
        this.result = res
    }
}