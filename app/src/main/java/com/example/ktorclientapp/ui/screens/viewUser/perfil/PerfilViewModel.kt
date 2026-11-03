package com.example.ktorclientapp.ui.screens.viewUser.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktorclientapp.data.model.CambioFoto
import com.example.ktorclientapp.data.model.CambioPassword
import com.example.ktorclientapp.data.model.User
import com.example.ktorclientapp.data.repository.GameRepository
import com.example.ktorclientapp.ui.screens.viewMain.login.toMd5
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val repositorio: GameRepository = GameRepository()
) : ViewModel() {

    // Es null hasta que llegan los datos del servidor
    private val _usuario = MutableStateFlow<User?>(null)
    val usuario: StateFlow<User?> = _usuario.asStateFlow()

    // Aquí sale todo lo que le queremos decir al usuario (errores y avisos)
    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    fun cargarPerfil(idUser: Int) {
        viewModelScope.launch {
            try {
                _usuario.value = repositorio.getUserById(idUser)
            } catch (e: Exception) {
                _mensaje.value = "No se pudo cargar el perfil"
            }
        }
    }

    fun cambiarPassword(idUser: Int, actual: String, nueva: String, confirmacion: String) {
        // Primero comprobamos lo básico sin molestar al servidor
        if (actual.isBlank() || nueva.isBlank() || confirmacion.isBlank()) {
            _mensaje.value = "Rellena los tres campos"
            return
        }
        if (nueva != confirmacion) {
            _mensaje.value = "La contraseña nueva y su confirmación no coinciden"
            return
        }

        viewModelScope.launch {
            try {
                // Al servidor nunca le mandamos la contraseña en claro, siempre en md5
                repositorio.cambiarPassword(idUser, CambioPassword(actual.toMd5(), nueva.toMd5()))
                _mensaje.value = "Contraseña cambiada correctamente"
            } catch (e: ClientRequestException) {
                _mensaje.value = e.response.bodyAsText()
            } catch (e: Exception) {
                _mensaje.value = "No se pudo conectar con el servidor"
            }
        }
    }

    fun guardarFoto(idUser: Int, url: String) {
        viewModelScope.launch {
            try {
                repositorio.cambiarFoto(idUser, CambioFoto(url))

                // Actualizamos la foto en pantalla sin tener que volver a pedir el perfil
                val usuarioActual = _usuario.value
                if (usuarioActual != null) {
                    _usuario.value = usuarioActual.copy(foto = url)
                }
                _mensaje.value = "Foto de perfil guardada"
            } catch (e: Exception) {
                _mensaje.value = "No se pudo guardar la foto en el servidor"
            }
        }
    }
}