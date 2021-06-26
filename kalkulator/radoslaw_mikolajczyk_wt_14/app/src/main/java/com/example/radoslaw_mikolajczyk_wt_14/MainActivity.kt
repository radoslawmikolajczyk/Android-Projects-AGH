package com.example.radoslaw_mikolajczyk_wt_14

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

/**
 * Radosław Mikołajczyk grupa wtorek godz.14
 *
 * W swoim rozwiazaniu nie wykonalem zalozen z podpunktów: 5 oraz 9
 * Cala reszta wedlug mnie jest w porzadku, w razie watpliwosci prosze o kontakt
 */

class MainActivity : AppCompatActivity() {
    private val buttons = ArrayList<Button>()
    private val specialButtons = HashMap<Button, String>()
    private lateinit var textView: TextView
    private var op = Operations()
    private var inputWrap = InputWrapper()
    private var lastOperation = ""
    private var isFirst = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getAllButtons(findViewById(R.id.root))
        getAllSpecialButtons()
        textView = findViewById(R.id.resultTextId)
        addListeners(buttons, textView)
    }

    private fun getAllButtons(parent: ViewGroup) {
        for(i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child is Button) {
                buttons.add(child)
            } else if (child is ViewGroup ){
                getAllButtons(child)
            }
        }
    }

    private fun getAllSpecialButtons() {
        for (button in buttons){
            when (button.id) {
                R.id.clearButtonId -> specialButtons[button] = "clear"
                R.id.sqrtButtonId -> specialButtons[button] = "sqrt"
                R.id.percentButtonId -> specialButtons[button] = "percent"
                R.id.divideButtonId -> specialButtons[button] = "/"
                R.id.multiplyButtonId -> specialButtons[button] = "*"
                R.id.minusButtonId -> specialButtons[button] = "-"
                R.id.plusButtonId -> specialButtons[button] = "+"
                R.id.equalsButtonId -> specialButtons[button] = "="
                R.id.signButtonId -> specialButtons[button] = "sign"
                R.id.dotButtonId -> specialButtons[button] = "."
            }
        }
    }

    private fun addListeners(buttons: ArrayList<Button>, textView: TextView) {
        for (button in buttons) {
            if (button !in specialButtons) {
                button.setOnClickListener {
                    if (textView.text.toString().length <= 9 && !op.getResult().isNaN()) {
                        updateView()
                        inputWrap.updateInput(button.text.toString())
                    }
                    updateView()
                }
            } else if (button in specialButtons) {
                when (specialButtons[button]) {
                    "clear" -> {
                        button.setOnClickListener {
                            inputWrap.clear()
                            op.clear()
                            isFirst = true
                            updateView()
                        }
                    }
                    "=" -> {
                        button.setOnClickListener {
                            if (op.getResult() != 0.0 && !op.getResult().isNaN()){
                                when (lastOperation) {
                                    "+" -> op.addNumber(inputWrap.getInput().toDouble())
                                    "-" -> op.subNumber(inputWrap.getInput().toDouble())
                                    "*" -> op.mulNumber(inputWrap.getInput().toDouble())
                                    "/" -> op.divNumber(inputWrap.getInput().toDouble())
                                }
                                inputWrap.setInput(op.getResult().toString())
                            }
                            updateView()
                            isFirst = true
                        }
                    }
                    "sign" -> {
                        button.setOnClickListener {
                            if(inputWrap.getInput().isNotEmpty() && !op.getResult().isNaN()) {
                                op.setResult(inputWrap.getInput().toDouble())
                                op.changeSign()
                                inputWrap.clear()
                                inputWrap.setInput(op.getResult().toString())
                                updateView()
                            }
                        }
                    }
                    "+" -> {
                        button.setOnClickListener {
                            simpleMathCalcValidation(specialButtons[button])
                        }
                    }
                    "-" -> {
                        button.setOnClickListener {
                            simpleMathCalcValidation(specialButtons[button])
                        }
                    }
                    "/" -> {
                        button.setOnClickListener {
                            simpleMathCalcValidation(specialButtons[button])
                        }
                    }
                    "*" -> {
                        button.setOnClickListener {
                            simpleMathCalcValidation(specialButtons[button])
                        }
                    }
                    "sqrt" -> {
                        button.setOnClickListener {
                            if (inputWrap.getInput().isNotEmpty() && !op.getResult().isNaN()) {
                                op.sqrtNumber(inputWrap.getInput().toDouble())
                                inputWrap.setInput(op.getResult().toString())
                                updateView()
                            } else {
                                inputWrap.clear()
                            }
                        }
                    }
                    "percent" -> {
                        button.setOnClickListener {
                            if (inputWrap.getInput().isNotEmpty() && !op.getResult().isNaN()) {
                                op.percentNumber(inputWrap.getInput().toDouble())
                                inputWrap.setInput(op.getResult().toString())
                                updateView()
                                isFirst = true
                            } else {
                                inputWrap.clear()
                            }
                        }
                    }
                    "." -> {
                        button.setOnClickListener {
                            if (inputWrap.getInput().isEmpty() || inputWrap.getInput() == "0") {
                                inputWrap.setInput("0.")
                                updateView()
                            } else if (!inputWrap.getInput().contains(".") && !op.getResult().isNaN()) {
                                inputWrap.updateInput(".")
                                updateView()
                            }
                        }
                    }
                }
            }
        }
    }
    
    private fun updateView() {
        if (inputWrap.getInput().length > 9) {
            val formattedStr = String.format("%.2E", inputWrap.getInput().toDouble())
            this.textView.text = formattedStr
        } else {
            this.textView.text = inputWrap.getInput()
        }
    }

    private fun simpleMathCalcValidation(sign: String?) {
        if (inputWrap.getInput().isNotEmpty() && !op.getResult().isNaN()) {
            if (isFirst){
                op.setResult(inputWrap.getInput().toDouble())
                isFirst = false
            } else {
                when (sign) {
                    "+" -> op.addNumber(inputWrap.getInput().toDouble())
                    "-" -> op.subNumber(inputWrap.getInput().toDouble())
                    "/" -> op.divNumber(inputWrap.getInput().toDouble())
                    "*" -> op.mulNumber(inputWrap.getInput().toDouble())
                }
            }
            inputWrap.setInput(op.getResult().toString())
            updateView()
            inputWrap.clear()
            if (sign != null) {
                lastOperation = sign
            }
        } else {
            inputWrap.clear()
        }
    }
}