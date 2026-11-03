package com.example.ktorclientapp.ui.screens.viewMain.login

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

    // alExito nos dice a dónde ir cuando el login es correcto (id y rol del usuario)
    fun iniciarSesion(correo: String, contrasena: String, alExito: (Int, String) -> Unit) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                // Por ahora el servidor no tiene /login, así que traemos los usuarios
                // y buscamos el que tenga ese correo y esa contraseña (en md5)
                val usuarios = repositorio.getUsers()
                val usuario = usuarios.find {
                    it.email == correo.trim() && it.password == contrasena.toMd5()
                }

                if (usuario != null) {
                    alExito(usuario.idUser, usuario.rol)
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

// Pasa un texto a md5, es la misma función que vimos en clase
fun String.toMd5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digested = md.digest(this.byteInputStream().readBytes())
    return digested.joinToString("") { byte -> "%02x".format(byte) }
}
