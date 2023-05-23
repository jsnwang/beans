package com.moo.beans.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.math.RoundingMode

class BeansViewModel: ViewModel() {
    val tip =  mutableStateOf("")
    var total =  mutableStateOf("")
    val percent =  mutableStateOf(.15f)

//    fun setTotal(s: String) {
//        total.value = s.toDouble()
//    }
//
//    fun getTotal(): Double {
//        return total.value
//    }

    fun getTipPercentage(): String {

    }
    fun getTip(): String {
        return if (total.value > 0.toString()) {
            val calc: BigDecimal = (total.value.toBigDecimal() * percent.value.toBigDecimal())
            calc.setScale(0, RoundingMode.HALF_UP)
            tip.value = (calc * BigDecimal(100)).toString()
            return tip.value
        } else {
            "0"
        }
    }
    fun getTipPlusTotal(): String {
        return if (total.value > 0.toString() && tip.value > 0.toString()) {
            (total.value.toBigDecimal() + tip.value.toBigDecimal()).toString()
        } else {
            "0"
        }
    }

}