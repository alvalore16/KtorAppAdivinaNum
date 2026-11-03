package com.example.ktorclientapp.ui.screens.viewAdmin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ktorclientapp.data.model.User

@Composable
fun AdminScreen(
    idUser: Int,
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val usuarios by viewModel.usuarios.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()

    // Usuario que queremos eliminar.
    // null significa que no hay ninguno seleccionado.
    var usuarioAEliminar by remember { mutableStateOf<User?>(null) }

    // Usuario que estamos editando.
    // Si es null y el formulario está abierto, estamos creando uno nuevo.
    var usuarioAEditar by remember { mutableStateOf<User?>(null) }
    var mostrarFormulario by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Administración de usuarios",
            style = MaterialTheme.typography.headlineSmall
        )

        Button(
            onClick = {
                usuarioAEditar = null
                mostrarFormulario = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nuevo usuario")
        }

        if (cargando) {
            CircularProgressIndicator()
        }

        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = usuarios,
                key = { usuario -> usuario.idUser }
            ) { usuario ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = usuario.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(usuario.email)
                        Text("Rol: ${usuario.rol}")

                        Row(modifier = Modifier.fillMaxWidth()) {
                            TextButton(
                                onClick = { viewModel.cambiarRol(usuario) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cambiar rol")
                            }
                            TextButton(
                                onClick = {
                                    // Guardamos el usuario que vamos a editar
                                    usuarioAEditar = usuario
                                    mostrarFormulario = true
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Editar")
                            }
                            TextButton(
                                onClick = {
                                    // Guardamos el usuario que queremos eliminar
                                    usuarioAEliminar = usuario
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }

    // ---------- ALERT DIALOG ----------
    if (usuarioAEliminar != null) {
        AlertDialog(
            onDismissRequest = { usuarioAEliminar = null },
            title = { Text("Eliminar usuario") },
            text = {
                Text("¿Seguro que quieres eliminar a ${usuarioAEliminar!!.name}? También se borrarán sus partidas.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Eliminamos mediante el ViewModel
                        viewModel.eliminarUsuario(usuarioAEliminar!!.idUser, idUser)
                        // Cerramos el diálogo
                        usuarioAEliminar = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { usuarioAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // ---------- FORMULARIO (crear / editar) ----------
    if (mostrarFormulario) {
        FormularioUsuario(
            usuario = usuarioAEditar,
            onGuardar = { nombre, correo, contrasena, esAdmin ->
                if (usuarioAEditar == null) {
                    viewModel.crearUsuario(nombre, correo, contrasena, esAdmin)
                } else {
                    viewModel.actualizarUsuario(usuarioAEditar!!.idUser, nombre, correo, esAdmin)
                }
                mostrarFormulario = false
            },
            onCancelar = { mostrarFormulario = false }
        )
    }
}

@Composable
private fun FormularioUsuario(
    usuario: User?,
    onGuardar: (String, String, String, Boolean) -> Unit,
    onCancelar: () -> Unit
) {
    // Si estamos editando, rellenamos los campos con los datos del usuario
    var nombre by remember { mutableStateOf(if (usuario != null) usuario.name else "") }
    var correo by remember { mutableStateOf(if (usuario != null) usuario.email else "") }
    var contrasena by remember { mutableStateOf("") }
    var esAdmin by remember { mutableStateOf(usuario != null && usuario.rol == "ADMIN") }

    // La contraseña solo hace falta al crear un usuario nuevo
    val datosCompletos = nombre.isNotBlank() && correo.isNotBlank() && (usuario != null || contrasena.isNotBlank())

    AlertDialog(
        onDismissRequest = onCancelar,
        title = {
            if (usuario == null) {
                Text("Nuevo usuario")
            } else {
                Text("Editar usuario")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo") },
                    singleLine = true
                )

                if (usuario == null) {
                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = esAdmin,
                        onCheckedChange = { esAdmin = it }
                    )
                    Text("Administrador")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onGuardar(nombre, correo, contrasena, esAdmin) },
                enabled = datosCompletos
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}
