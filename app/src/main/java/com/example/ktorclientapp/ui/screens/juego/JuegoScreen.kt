package com.example.ktorclientapp.ui.screens.juego

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
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
fun JuegoScreen(
    idUser: Int,
    modifier: Modifier = Modifier,
    viewModel: JuegoViewModel = viewModel()
) {
    val partida by viewModel.partida.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    // Datos del formulario
    var pistas by remember { mutableStateOf(false) }
    var limitarIntentos by remember { mutableStateOf(false) }
    var maxIntentos by remember { mutableStateOf(5) }
    var texto by remember { mutableStateOf("") }

    val partidaActual = partida

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {

        if (partidaActual == null) {
            Text(text = "Nueva partida")
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = pistas, onCheckedChange = { pistas = it })
                Text("Activar pistas")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = limitarIntentos, onCheckedChange = { limitarIntentos = it })
                Text("Limitar intentos")
            }

            if (limitarIntentos) {
                Text("Intentos máximos: $maxIntentos")
                Slider(
                    value = maxIntentos.toFloat(),
                    onValueChange = { maxIntentos = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                viewModel.empezarPartida(idUser, pistas, limitarIntentos, maxIntentos)
            }) {
                Text("Empezar partida")
            }

            Text(text = mensaje, color = MaterialTheme.colorScheme.error)

        } else {
            // ---------- Partida ----------
            Text(text = "Adivina el número")
            Spacer(modifier = Modifier.height(8.dp))

            Text("Rango: de 1 a 100")
            Text("Intentos realizados: ${partidaActual.intentos}")
            if (partidaActual.limitarIntentos) {
                Text("Intentos restantes: ${partidaActual.maxIntentos - partidaActual.intentos}")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = mensaje)
            Spacer(modifier = Modifier.height(16.dp))

            if (partidaActual.activa) {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    label = { Text("Tu número") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    viewModel.enviarIntento(texto)
                    texto = ""
                }) {
                    Text("Enviar")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.salirDePartida() }) {
                    Text("Salir (podrás continuar después)")
                }
            } else {
                if (partidaActual.ganado) {
                    Text("¡Has ganado!", color = MaterialTheme.colorScheme.primary)
                } else {
                    Text("Has perdido", color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    viewModel.empezarPartida(idUser, pistas, limitarIntentos, maxIntentos)
                }) {
                    Text("Jugar otra vez")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.salirDePartida() }) {
                    Text("Volver")
                }
            }
        }
    }
}