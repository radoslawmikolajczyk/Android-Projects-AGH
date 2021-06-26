package com.example.radoslaw_mikolajczyk_wt_14

class InputWrapper {
    private var input: String = ""

    fun getInput(): String {
        return this.input
    }

    fun updateInput(add: String) {
        if(this.input.startsWith("0") && !this.input.startsWith("0.")){
            this.input = add
        } else {
            this.input += add
        }
    }

    fun setInput(add: String) {
        this.input = add
    }

    fun clear() {
        this.input = ""
    }
}