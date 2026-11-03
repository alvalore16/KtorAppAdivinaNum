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

    private val _usuarios = MutableStateFlow<List<User>>(emptyList())
    val usuarios: StateFlow<List<User>> = _usuarios.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    fun cargarUsuarios() {
        viewModelScope.launch {
            try {
                _usuarios.value = repositorio.getUsers()
            } catch (e: Exception) {
                _mensaje.value = "No se pudieron cargar los usuarios"
            }
        }
    }

    // GAMER pasa a ADMIN y ADMIN pasa a GAMER
    fun cambiarRol(usuario: User) {
        var nuevoRol = "ADMIN"
        if (usuario.rol == "ADMIN") {
            nuevoRol = "GAMER"
        }

        viewModelScope.launch {
            try {
                repositorio.cambiarRol(usuario.idUser, CambioRol(nuevoRol))
                _mensaje.value = "Rol cambiado"
                cargarUsuarios()
            } catch (e: ClientRequestException) {
                _mensaje.value = e.response.bodyAsText()
            } catch (e: Exception) {
                _mensaje.value = "No se pudo conectar con el servidor"
            }
        }
    }

    fun borrar(idBorrar: Int, miId: Int) {
        if (idBorrar == miId) {
            _mensaje.value = "No puedes borrarte a ti mismo"
            return
        }

        viewModelScope.launch {
            try {
                repositorio.borrarUsuario(idBorrar)
                _mensaje.value = "Usuario borrado"
                cargarUsuarios()
            } catch (e: ClientRequestException) {
                _mensaje.value = e.response.bodyAsText()
            } catch (e: Exception) {
                _mensaje.value = "No se pudo conectar con el servidor"
            }
        }
    }

    fun crear(nombre: String, correo: String, contrasena: String, esAdmin: Boolean) {
        var rol = "GAMER"
        if (esAdmin) {
            rol = "ADMIN"
        }

        viewModelScope.launch {
            try {
                repositorio.crearUsuarioAdmin(
                    User(
                        name = nombre.trim(),
                        email = correo.trim(),
                        password = contrasena.toMd5(),
                        rol = rol
                    )
                )
                _mensaje.value = "Usuario creado"
                cargarUsuarios()
            } catch (e: ClientRequestException) {
                _mensaje.value = e.response.bodyAsText()
            } catch (e: Exception) {
                _mensaje.value = "No se pudo conectar con el servidor"
            }
        }
    }

    fun editar(idUser: Int, nombre: String, correo: String, esAdmin: Boolean) {
        var rol = "GAMER"
        if (esAdmin) {
            rol = "ADMIN"
        }

        viewModelScope.launch {
            try {
                repositorio.editarUsuario(
                    idUser,
                    User(
                        idUser = idUser,
                        name = nombre.trim(),
                        email = correo.trim(),
                        rol = rol
                    )
                )
                _mensaje.value = "Usuario actualizado"
                cargarUsuarios()
            } catch (e: ClientRequestException) {
                _mensaje.value = e.response.bodyAsText()
            } catch (e: Exception) {
                _mensaje.value = "No se pudo conectar con el servidor"
            }
        }
    }
}
