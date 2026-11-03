package com.example.ktorclientapp.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ktorclientapp.R
import com.example.ktorclientapp.ui.screens.juego.JuegoScreen
import com.example.ktorclientapp.ui.screens.perfil.PerfilScreen
import com.example.ktorclientapp.ui.screens.perfil.PerfilViewModel

@Composable
fun MainScreen(idUser: Int, alCerrarSesion: () -> Unit, modifier: Modifier = Modifier) {
    var pestana by remember { mutableStateOf(0) }
    val perfilViewModel: PerfilViewModel = viewModel()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = pestana == 0,
                    onClick = { pestana = 0 },
                    icon = { Icon(painter = painterResource(R.drawable.ic_jugar), contentDescription = "Jugar") },
                    label = { Text("Jugar") }
                )
                NavigationBarItem(
                    selected = pestana == 1,
                    onClick = { pestana = 1 },
                    icon = { Icon(painter = painterResource(R.drawable.ic_ranking), contentDescription = "Ranking") },
                    label = { Text("Ranking") }
                )
                NavigationBarItem(
                    selected = pestana == 2,
                    onClick = {
                        pestana = 2
                        perfilViewModel.cargarPerfil(idUser)
                    },
                    icon = { Icon(painter = painterResource(R.drawable.ic_perfil), contentDescription = "Perfil") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (pestana == 0) {
                JuegoScreen(idUser = idUser)
            }
            if (pestana == 1) {
                Text("Aquí irá el ranking")
            }
            if (pestana == 2) {
                PerfilScreen(
                    idUser = idUser,
                    viewModel = perfilViewModel,
                    alCerrarSesion = alCerrarSesion
                )
            }
        }
    }
}