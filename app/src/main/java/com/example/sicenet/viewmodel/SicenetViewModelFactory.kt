package com.example.sicenet.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sicenet.data.SicenetLocalRepository

class SicenetViewModelFactory(
    private val repository: SicenetLocalRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KardexViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KardexViewModel(repository, context) as T
        }
        // Aquí agregarías los otros ViewModels (Unidades, Finales, etc.)
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}