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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun JuegoScreen(
    modifier: Modifier = Modifier,
    viewModel: JuegoViewModel = viewModel()
) {
    val pistas by viewModel.pistas.collectAsState()
    val limitarIntentos by viewModel.limitarIntentos.collectAsState()
    val maxIntentos by viewModel.maxIntentos.collectAsState()
    val partidaIniciada by viewModel.partidaIniciada.collectAsState()

    if (partidaIniciada) {
        // De momento solo un resumen. En el Paso 4 irá aquí el juego.
        Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
            Text(text = "Partida en marcha", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            if (pistas) {
                Text("Pistas: activadas")
            } else {
                Text("Pistas: desactivadas")
            }
            if (limitarIntentos) {
                Text("Intentos máximos: $maxIntentos")
            } else {
                Text("Intentos: sin límite")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.abandonarPartida() }) {
                Text("Abandonar")
            }
        }
    } else {
        ConfiguracionPartida(
            pistas = pistas,
            limitarIntentos = limitarIntentos,
            maxIntentos = maxIntentos,
            alCambiarPistas = { viewModel.cambiarPistas(it) },
            alCambiarLimitar = { viewModel.cambiarLimitarIntentos(it) },
            alCambiarMaxIntentos = { viewModel.cambiarMaxIntentos(it) },
            alEmpezar = { viewModel.empezarPartida() },
            modifier = modifier
        )
    }
}

@Composable
private fun ConfiguracionPartida(
    pistas: Boolean,
    limitarIntentos: Boolean,
    maxIntentos: Int,
    alCambiarPistas: (Boolean) -> Unit,
    alCambiarLimitar: (Boolean) -> Unit,
    alCambiarMaxIntentos: (Int) -> Unit,
    alEmpezar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Nueva partida", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = pistas, onCheckedChange = alCambiarPistas)
            Text("Activar pistas")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = limitarIntentos, onCheckedChange = alCambiarLimitar)
            Text("Limitar intentos")
        }

        if (limitarIntentos) {
            Text("Intentos máximos: $maxIntentos")
            Slider(
                value = maxIntentos.toFloat(),
                onValueChange = { alCambiarMaxIntentos(it.toInt()) },
                valueRange = 1f..10f,
                steps = 8
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = alEmpezar) {
            Text("Empezar partida")
        }
    }
}