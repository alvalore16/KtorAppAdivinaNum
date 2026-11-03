package com.example.ktorclientapp.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktorclientapp.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

class LoginViewModel(
    private val repositorio: GameRepository = GameRepository()
) : ViewModel() {

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun iniciarSesion(correo: String, contrasena: String, alExito: (Int) -> Unit) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                // PROVISIONAL: se comprueba aquí porque el servidor aún no tiene /login
                val usuario = repositorio.getUsers()
                    .find { it.email == correo.trim() && it.password == contrasena.toMd5() }

                if (usuario != null) {
                    alExito(usuario.idUser)
                } else {
                    _error.value = "Correo o contraseña incorrectos"
                }
            } catch (e: Exception) {
                _error.value = "No se pudo conectar con el servidor"
            } finally {
                _cargando.value = false
            }
        }
    }
}

fun String.toMd5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digested = md.digest(this.byteInputStream().readBytes())
    return digested.joinToString("") { byte -> "%02x".format(byte) }
}