package com.example.ktorclientapp.ui.screens.viewMain.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktorclientapp.data.model.User
import com.example.ktorclientapp.data.repository.GameRepository
import com.example.ktorclientapp.ui.screens.viewMain.login.toMd5
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
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

    fun registrar(
        nombre: String,
        correo: String,
        contrasena: String,
        confirmacion: String,
        fechaNacimiento: String,
        alExito: () -> Unit
    ) {
        if (contrasena != confirmacion) {
            _error.value = "Las contraseñas no coinciden"
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                repositorio.createUser(
                    User(
                        name = nombre.trim(),
                        email = correo.trim(),
                        password = contrasena.toMd5(),
                        fechaNacimiento = fechaNacimiento
                    )
                )
                alExito()
            } catch (e: ClientRequestException) {
                _error.value = e.response.bodyAsText()
            } catch (e: Exception) {
                _error.value = "No se pudo completar el registro"
            } finally {
                _cargando.value = false
            }
        }
    }
}