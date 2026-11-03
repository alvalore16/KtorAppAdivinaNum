package com.example.ktorclientapp.ui.screens.perfil

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun PerfilScreen(
    idUser: Int,
    viewModel: PerfilViewModel,
    alCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario by viewModel.usuario.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    var passwordActual by remember { mutableStateOf("") }
    var passwordNueva by remember { mutableStateOf("") }
    var passwordConfirmacion by remember { mutableStateOf("") }

    val usuarioActual = usuario

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Mi perfil", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (usuarioActual == null) {
            Text("Cargando perfil...")
        } else {
            Text("Nombre: ${usuarioActual.name}")
            Text("Correo: ${usuarioActual.email}")
            Text("Rol: ${usuarioActual.rol}")
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Cambiar contraseña", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = passwordActual,
            onValueChange = { passwordActual = it },
            label = { Text("Contraseña actual") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = passwordNueva,
            onValueChange = { passwordNueva = it },
            label = { Text("Contraseña nueva") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = passwordConfirmacion,
            onValueChange = { passwordConfirmacion = it },
            label = { Text("Repite la contraseña nueva") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            viewModel.cambiarPassword(idUser, passwordActual, passwordNueva, passwordConfirmacion)
            passwordActual = ""
            passwordNueva = ""
            passwordConfirmacion = ""
        }) {
            Text("Cambiar contraseña")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = mensaje)

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = alCerrarSesion) {
            Text("Cerrar sesión")
        }
    }
}