package com.example.ktorclientapp.ui.screens.viewUser.ranking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ktorclientapp.data.model.RankingItem

@Composable
fun RankingScreen(
    viewModel: RankingViewModel,
    modifier: Modifier = Modifier
) {
    val ranking by viewModel.ranking.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Ranking",
            style = MaterialTheme.typography.headlineMedium
        )

        if (cargando) {
            CircularProgressIndicator()
        }

        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (!cargando && error == null && ranking.isEmpty()) {
            Text("Todavía no hay partidas terminadas")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(
                items = ranking,
                key = { jugador -> jugador.idUser }
            ) { jugador ->
                // La posición es su sitio en la lista + 1
                TarjetaJugador(
                    posicion = ranking.indexOf(jugador) + 1,
                    jugador = jugador
                )
            }
        }
    }
}

@Composable
private fun TarjetaJugador(posicion: Int, jugador: RankingItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$posicion",
                style = MaterialTheme.typography.titleLarge
            )

            AsyncImage(
                model = jugador.foto,
                contentDescription = "Foto de ${jugador.name}",
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = jugador.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text("${jugador.ganadas} ganadas de ${jugador.jugadas} partidas")
                Text("${jugador.porcentaje.toInt()}% de victorias")
            }
        }
    }
}
