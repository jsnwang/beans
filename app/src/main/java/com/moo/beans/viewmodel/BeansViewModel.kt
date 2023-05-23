package com.moo.beans.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.math.RoundingMode

class BeansViewModel: ViewModel() {
    val tip =  mutableStateOf("")
    var total =  mutableStateOf("")
    val percent =  mutableStateOf(15f)

//    fun setTotal(s: String) {
//        total.value = s.toDouble()
//    }
//
//    fun getTotal(): Double {
//        return total.value
//    }

    fun getTipPercentage(): Float {
        return percent.value
    }

    fun getTip(): String {
        return if (total.value > 0.toString()) {
            val calc = (total.value.toFloat().times(percent.value.div(100))).toBigDecimal()
            tip.value = calc.setScale(2, RoundingMode.HALF_UP).toString()
            return tip.value
        } else {
            "0"
        }
    }
    fun getTipPlusTotal(): String {
        return if (total.value > 0.toString() && tip.value > 0.toString()) {
            (total.value.toBigDecimal() + tip.value.toBigDecimal()).setScale(2, RoundingMode.HALF_UP).toString()
        } else {
            "0"
        }
    }

}