package com.example.ktorclientapp.ui.screens.ranking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
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

@Composable
fun RankingScreen(viewModel: RankingViewModel, modifier: Modifier = Modifier) {
    val ranking by viewModel.ranking.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Ranking", style = MaterialTheme.typography.headlineMedium)

        if (mensaje != "") {
            Text(mensaje)
        }
        if (ranking.isEmpty() && mensaje == "") {
            Text("Todavía no hay partidas terminadas")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(ranking) { indice, item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${indice + 1}", style = MaterialTheme.typography.titleLarge)
                        AsyncImage(
                            model = item.foto,
                            contentDescription = "Foto de ${item.name}",
                            modifier = Modifier.padding(start = 12.dp).size(48.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(item.name, style = MaterialTheme.typography.titleMedium)
                            Text("${item.ganadas} ganadas de ${item.jugadas} partidas")
                            Text("${item.porcentaje.toInt()}% de victorias")
                        }
                    }
                }
            }
        }
    }
}
