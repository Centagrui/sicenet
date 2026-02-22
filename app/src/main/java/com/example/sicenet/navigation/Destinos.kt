package com.example.sicenet.navigation

sealed class Destinos(val ruta: String, val titulo: String) {
    object Perfil : Destinos("perfil", "Mi Perfil")
    object Carga : Destinos("carga", "Carga Académica")
    object Kardex : Destinos("kardex", "Kárdex")
}