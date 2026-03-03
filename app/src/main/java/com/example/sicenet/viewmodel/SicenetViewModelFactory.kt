package com.example.sicenet.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sicenet.data.SicenetLocalRepository

/**
 * Fábrica personalizada para instanciar ViewModels con dependencias.
 * Se encarga de inyectar el repositorio y el contexto en los ViewModels.
 */
class SicenetViewModelFactory(
    private val repository: SicenetLocalRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verificamos si la clase que se intenta crear es KardexViewModel
        if (modelClass.isAssignableFrom(KardexViewModel::class.java)) {
            // Retornamos una nueva instancia pasando las dependencias requeridas
            return KardexViewModel(repository, context) as T
        }

        // Aquí es donde escalarías tu app:
        // if (modelClass.isAssignableFrom(UnidadesViewModel::class.java)) { ... }

        throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
    }
}