package com.example.ktorclientapp.ui.screens.perfil

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktorclientapp.data.model.CambioFoto
import com.example.ktorclientapp.data.model.CambioPassword
import com.example.ktorclientapp.data.model.User
import com.example.ktorclientapp.data.repository.GameRepository
import com.example.ktorclientapp.data.repository.ImageRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val repositorio: GameRepository = GameRepository()
) : ViewModel() {

    // null hasta que se cargan los datos del servidor
    private val _usuario = MutableStateFlow<User?>(null)
    val usuario: StateFlow<User?> = _usuario.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    // Indica si la subida de la foto está en curso
    private val _subiendo = MutableStateFlow(false)
    val subiendo: StateFlow<Boolean> = _subiendo.asStateFlow()

    fun cargarPerfil(idUser: Int) {
        viewModelScope.launch {
            try {
                _usuario.value = repositorio.getUserById(idUser)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value = "No se pudo cargar el perfil"
            }
        }
    }

    fun cambiarPassword(idUser: Int, actual: String, nueva: String, confirmacion: String) {
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
                repositorio.cambiarPassword(idUser, CambioPassword(actual, nueva))
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

                val usuarioActual = _usuario.value
                if (usuarioActual != null) {
                    _usuario.value = usuarioActual.copy(foto = url)
                }
                _mensaje.value = "Foto de perfil guardada"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value = "No se pudo guardar la foto en el servidor"
            }
        }
    }
}