package com.example.ktorclientapp.ui.screens.viewUser.juego

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ktorclientapp.data.model.Games

@Composable
fun JuegoScreen(
    idUser: Int,
    modifier: Modifier = Modifier,
    viewModel: JuegoViewModel = viewModel()
) {
    val partida by viewModel.partida.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    // Lo que el jugador elige antes de empezar
    var pistas by remember { mutableStateOf(false) }
    var limitarIntentos by remember { mutableStateOf(false) }
    var maxIntentos by remember { mutableFloatStateOf(5f) }

    val partidaActual = partida

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (partidaActual == null) {
            SeccionNuevaPartida(
                pistas = pistas,
                onPistasChange = { pistas = it },
                limitarIntentos = limitarIntentos,
                onLimitarIntentosChange = { limitarIntentos = it },
                maxIntentos = maxIntentos,
                onMaxIntentosChange = { maxIntentos = it },
                mensaje = mensaje,
                onEmpezar = {
                    viewModel.empezarPartida(idUser, pistas, limitarIntentos, maxIntentos.toInt())
                }
            )
        } else {
            SeccionPartida(
                partida = partidaActual,
                mensaje = mensaje,
                onEnviar = { texto -> viewModel.enviarIntento(texto) },
                onJugarOtra = {
                    viewModel.empezarPartida(idUser, pistas, limitarIntentos, maxIntentos.toInt())
                },
                onSalir = { viewModel.salirDePartida() }
            )
        }
    }
}

@Composable
private fun SeccionNuevaPartida(
    pistas: Boolean,
    onPistasChange: (Boolean) -> Unit,
    limitarIntentos: Boolean,
    onLimitarIntentosChange: (Boolean) -> Unit,
    maxIntentos: Float,
    onMaxIntentosChange: (Float) -> Unit,
    mensaje: String,
    onEmpezar: () -> Unit
) {
    Text(
        text = "Adivina el número",
        style = MaterialTheme.typography.headlineMedium
    )
    Text("Piensa rápido, el número está entre 1 y 100")
    Spacer(modifier = Modifier.height(24.dp))

    // Tarjeta con las opciones de la partida
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Opciones",
                style = MaterialTheme.typography.titleMedium
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = pistas, onCheckedChange = onPistasChange)
                Text("Activar pistas (mayor / menor)")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = limitarIntentos, onCheckedChange = onLimitarIntentosChange)
                Text("Limitar intentos")
            }

            // El slider solo sale si quiere limitar los intentos
            if (limitarIntentos) {
                Text("Intentos máximos: ${maxIntentos.toInt()}")
                Slider(
                    value = maxIntentos,
                    onValueChange = onMaxIntentosChange,
                    valueRange = 1f..10f,
                    steps = 8
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onEmpezar,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Empezar partida")
    }

    if (mensaje != "") {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = mensaje, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun SeccionPartida(
    partida: Games,
    mensaje: String,
    onEnviar: (String) -> Unit,
    onJugarOtra: () -> Unit,
    onSalir: () -> Unit
) {
    // Número que está escribiendo ahora mismo
    var texto by remember { mutableStateOf("") }

    Text(
        text = "Adivina el número",
        style = MaterialTheme.typography.headlineMedium
    )
    Text("Rango: de 1 a 100")
    Spacer(modifier = Modifier.height(16.dp))

    // Tarjeta con los intentos
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${partida.intentos}",
                style = MaterialTheme.typography.displayMedium
            )
            Text("intentos realizados")

            if (partida.limitarIntentos) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Te quedan ${partida.maxIntentos - partida.intentos}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Aquí sale la pista o el resultado
    Text(
        text = mensaje,
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(16.dp))

    if (partida.activa) {
        OutlinedTextField(
            value = texto,
            onValueChange = { texto = it },
            label = { Text("Tu número") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                onEnviar(texto)
                // Vaciamos la caja para el siguiente intento
                texto = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onSalir,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salir (podrás continuar después)")
        }
    } else {
        // La partida ya ha terminado
        if (partida.ganado) {
            Text(
                text = "¡Has ganado!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Text(
                text = "Has perdido",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onJugarOtra,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Jugar otra vez")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onSalir,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
