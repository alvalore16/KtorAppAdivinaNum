package com.example.ktorclientapp.ui.screens.viewAdmin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktorclientapp.data.model.CambioRol
import com.example.ktorclientapp.data.model.User
import com.example.ktorclientapp.data.repository.GameRepository
import com.example.ktorclientapp.ui.screens.viewMain.login.toMd5
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repositorio: GameRepository = GameRepository()
) : ViewModel() {

    // Usuarios que se pintan en la lista
    private val _usuarios = MutableStateFlow<List<User>>(emptyList())
    val usuarios: StateFlow<List<User>> = _usuarios.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargarUsuarios() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                _usuarios.value = repositorio.getUsers()
            } catch (e: Exception) {
                _error.value = "No se pudieron cargar los usuarios"
            } finally {
                _cargando.value = false
            }
        }
    }

    // Si es GAMER lo pasamos a ADMIN y al revés
    fun cambiarRol(usuario: User) {
        var nuevoRol = "ADMIN"
        if (usuario.rol == "ADMIN") {
            nuevoRol = "GAMER"
        }

        viewModelScope.launch {
            _error.value = null

            try {
                repositorio.cambiarRol(usuario.idUser, CambioRol(nuevoRol))
                cargarUsuarios()
            } catch (e: ClientRequestException) {
                // El servidor explica qué ha fallado (por ejemplo, último admin)
                _error.value = e.response.bodyAsText()
            } catch (e: Exception) {
                _error.value = "No se pudo conectar con el servidor"
            }
        }
    }

    fun eliminarUsuario(id: Int, miId: Int) {
        // Para no cargarnos nuestra propia cuenta
        if (id == miId) {
            _error.value = "No puedes eliminarte a ti mismo"
            return
        }

        viewModelScope.launch {
            _error.value = null

            try {
                repositorio.eliminarUsuario(id)
                cargarUsuarios()
            } catch (e: ClientRequestException) {
                _error.value = e.response.bodyAsText()
            } catch (e: Exception) {
                _error.value = "No se pudo conectar con el servidor"
            }
        }
    }

    fun crearUsuario(nombre: String, correo: String, contrasena: String, esAdmin: Boolean) {
        var rol = "GAMER"
        if (esAdmin) {
            rol = "ADMIN"
        }

        viewModelScope.launch {
            _error.value = null

            try {
                val usuario = User(
                    name = nombre.trim(),
                    email = correo.trim(),
                    password = contrasena.toMd5(),
                    rol = rol
                )
                repositorio.crearUsuarioAdmin(usuario)
                cargarUsuarios()
            } catch (e: ClientRequestException) {
                _error.value = e.response.bodyAsText()
            } catch (e: Exception) {
                _error.value = "No se pudo conectar con el servidor"
            }
        }
    }

    fun actualizarUsuario(id: Int, nombre: String, correo: String, esAdmin: Boolean) {
        var rol = "GAMER"
        if (esAdmin) {
            rol = "ADMIN"
        }

        viewModelScope.launch {
            _error.value = null

            try {
                val usuario = User(
                    idUser = id,
                    name = nombre.trim(),
                    email = correo.trim(),
                    rol = rol
                )
                repositorio.actualizarUsuario(id, usuario)
                cargarUsuarios()
            } catch (e: ClientRequestException) {
                _error.value = e.response.bodyAsText()
            } catch (e: Exception) {
                _error.value = "No se pudo conectar con el servidor"
            }
        }
    }
}
