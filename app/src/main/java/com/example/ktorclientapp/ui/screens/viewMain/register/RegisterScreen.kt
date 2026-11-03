package com.example.ktorclientapp.ui.screens.viewMain.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    alRegistrarse: () -> Unit,
    alVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }
    // Para abrir y cerrar el calendario
    var mostrarCalendario by remember { mutableStateOf(false) }

    // El calendario nos da la fecha en milisegundos, la pasamos a texto dd/MM/yyyy
    val estadoFecha = rememberDatePickerState()
    val milisegundos = estadoFecha.selectedDateMillis

    var fechaNacimiento = ""
    if (milisegundos != null) {
        fechaNacimiento = SimpleDateFormat("dd/MM/yyyy").format(Date(milisegundos))
    }

    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmacion,
            onValueChange = { confirmacion = it },
            label = { Text("Confirmar contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = { mostrarCalendario = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (fechaNacimiento == "") {
                Text("Elegir fecha de nacimiento")
            } else {
                Text("Fecha de nacimiento: $fechaNacimiento")
            }
        }

        Spacer(Modifier.height(16.dp))

        if (cargando) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    viewModel.registrar(
                        nombre,
                        correo,
                        contrasena,
                        confirmacion,
                        fechaNacimiento,
                        alRegistrarse
                    )
                },
                // No dejamos pulsar hasta que todo esté relleno
                enabled = nombre.isNotBlank() && correo.isNotBlank() && contrasena.isNotBlank()
                        && confirmacion.isNotBlank() && fechaNacimiento.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = alVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio de sesión")
        }

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
        }
    }

    // ---------- CALENDARIO ----------
    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = estadoFecha)
        }
    }
}