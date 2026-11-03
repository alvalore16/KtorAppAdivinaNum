package com.example.ktorclientapp.ui.screens.viewMain.recuperar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktorclientapp.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecuperarViewModel(
    private val repositorio: GameRepository = GameRepository()
) : ViewModel() {

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    fun enviar(correo: String) {
        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = ""

            try {
                repositorio.recuperarPassword(correo.trim())
                _mensaje.value = "Se ha enviado una contraseña temporal."
            } catch (e: Exception) {
                _mensaje.value = "No se pudo conectar con el servidor"
            } finally {
                _cargando.value = false
            }
        }
    }
}
