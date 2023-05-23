package com.moo.beans.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class BeansViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(BeansViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BeansViewModel() as T
        }
        throw IllegalArgumentException("ViewModel not found")
    }
}