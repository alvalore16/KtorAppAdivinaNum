package com.example.ktorclientapp.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktorclientapp.data.model.User
import com.example.ktorclientapp.data.repository.GameRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repositorio: GameRepository = GameRepository()
) : ViewModel() {

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun registrar(nombre: String, correo: String, contrasena: String, alExito: () -> Unit) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                repositorio.createUser(
                    User(
                        name = nombre.trim(),
                        email = correo.trim(),
//                        password = contrasena.toMd5()
                    )
                )
                alExito()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudo completar el registro"
            } finally {
                _cargando.value = false
            }
        }
    }
}