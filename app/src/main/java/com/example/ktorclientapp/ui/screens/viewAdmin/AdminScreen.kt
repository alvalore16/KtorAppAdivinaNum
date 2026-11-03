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
import androidx.compose.material3.Checkbox
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
    val mensaje by viewModel.mensaje.collectAsState()

    var usuarioABorrar by remember { mutableStateOf<User?>(null) }
    var usuarioEditando by remember { mutableStateOf<User?>(null) }
    var mostrarFormulario by remember { mutableStateOf(false) }

    val borrar = usuarioABorrar
    val editando = usuarioEditando

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Administración", style = MaterialTheme.typography.headlineMedium)

        Button(
            onClick = {
                usuarioEditando = null
                mostrarFormulario = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nuevo usuario")
        }

        if (mensaje != "") {
            Text(mensaje)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(usuarios) { usuario ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(usuario.name, style = MaterialTheme.typography.titleMedium)
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
                                    usuarioEditando = usuario
                                    mostrarFormulario = true
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Editar")
                            }
                            TextButton(
                                onClick = { usuarioABorrar = usuario },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Borrar")
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmación antes de borrar
    if (borrar != null) {
        AlertDialog(
            onDismissRequest = { usuarioABorrar = null },
            title = { Text("Borrar usuario") },
            text = { Text("¿Seguro que quieres borrar a ${borrar.name}? También se borrarán sus partidas.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.borrar(borrar.idUser, idUser)
                        usuarioABorrar = null
                    }
                ) {
                    Text("Borrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { usuarioABorrar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Formulario para crear (editando == null) o editar un usuario
    if (mostrarFormulario) {
        FormularioUsuario(
            usuario = editando,
            alGuardar = { nombre, correo, contrasena, esAdmin ->
                if (editando == null) {
                    viewModel.crear(nombre, correo, contrasena, esAdmin)
                } else {
                    viewModel.editar(editando.idUser, nombre, correo, esAdmin)
                }
                mostrarFormulario = false
            },
            alCancelar = { mostrarFormulario = false }
        )
    }
}

@Composable
private fun FormularioUsuario(
    usuario: User?,
    alGuardar: (String, String, String, Boolean) -> Unit,
    alCancelar: () -> Unit
) {
    var nombre by remember { mutableStateOf(if (usuario != null) usuario.name else "") }
    var correo by remember { mutableStateOf(if (usuario != null) usuario.email else "") }
    var contrasena by remember { mutableStateOf("") }
    var esAdmin by remember { mutableStateOf(usuario != null && usuario.rol == "ADMIN") }

    // La contraseña solo se pide al crear
    val datosCompletos = nombre.isNotBlank() && correo.isNotBlank() && (usuario != null || contrasena.isNotBlank())

    AlertDialog(
        onDismissRequest = alCancelar,
        title = { Text(if (usuario == null) "Nuevo usuario" else "Editar usuario") },
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
                    Checkbox(checked = esAdmin, onCheckedChange = { esAdmin = it })
                    Text("Administrador")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { alGuardar(nombre, correo, contrasena, esAdmin) },
                enabled = datosCompletos
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = alCancelar) {
                Text("Cancelar")
            }
        }
    )
}
