package com.example.ktorclientapp.ui.screens.viewMain.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    alIniciarSesion: (Int, String) -> Unit,
    irRegistro: () -> Unit,
    irRecuperar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel()
) {
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()

    // Lo que el usuario va escribiendo
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Mientras carga enseñamos la ruedita en vez del botón
        if (cargando) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.iniciarSesion(correo, contrasena, alIniciarSesion) },
                enabled = correo.isNotBlank() && contrasena.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar")
            }
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = irRegistro,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear una cuenta")
        }

        TextButton(
            onClick = irRecuperar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿Has olvidado tu contraseña?")
        }
    }
}
