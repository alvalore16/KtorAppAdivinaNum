package com.example.ktorclientapp.ui.screens.juego

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class JuegoViewModel : ViewModel() {

    private val _pistas = MutableStateFlow(false)
    val pistas: StateFlow<Boolean> = _pistas.asStateFlow()

    private val _limitarIntentos = MutableStateFlow(false)
    val limitarIntentos: StateFlow<Boolean> = _limitarIntentos.asStateFlow()

    private val _maxIntentos = MutableStateFlow(5)
    val maxIntentos: StateFlow<Int> = _maxIntentos.asStateFlow()

    private val _partidaIniciada = MutableStateFlow(false)
    val partidaIniciada: StateFlow<Boolean> = _partidaIniciada.asStateFlow()

    fun cambiarPistas(valor: Boolean) {
        _pistas.value = valor
    }

    fun cambiarLimitarIntentos(valor: Boolean) {
        _limitarIntentos.value = valor
    }

    fun cambiarMaxIntentos(valor: Int) {
        _maxIntentos.value = valor
    }

    fun empezarPartida() {
        _partidaIniciada.value = true
    }

    fun abandonarPartida() {
        _partidaIniciada.value = false
    }
}