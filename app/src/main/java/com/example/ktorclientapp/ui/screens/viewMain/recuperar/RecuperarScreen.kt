package com.example.ktorclientapp.ui.screens.viewMain.recuperar

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RecuperarScreen(
    alVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecuperarViewModel = viewModel()
) {
    var correo by remember { mutableStateOf("") }

    val cargando by viewModel.cargando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Recuperar contraseña",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Escribe tu correo y te enviaremos una contraseña temporal.")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (cargando) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.enviar(correo) },
                enabled = correo.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar")
            }
        }

        if (mensaje != "") {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = mensaje)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = alVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio de sesión")
        }
    }
}
