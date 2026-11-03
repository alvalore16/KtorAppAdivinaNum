package com.example.ktorclientapp.ui.screens.main

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ktorclientapp.R
import com.example.ktorclientapp.ui.screens.viewAdmin.AdminScreen
import com.example.ktorclientapp.ui.screens.viewAdmin.AdminViewModel
import com.example.ktorclientapp.ui.screens.viewUser.juego.JuegoScreen
import com.example.ktorclientapp.ui.screens.viewUser.perfil.ImageViewModel
import com.example.ktorclientapp.ui.screens.viewUser.perfil.ImageViewModelFactory
import com.example.ktorclientapp.ui.screens.viewUser.perfil.PerfilScreen
import com.example.ktorclientapp.ui.screens.viewUser.perfil.PerfilViewModel
import com.example.ktorclientapp.ui.screens.viewUser.ranking.RankingScreen
import com.example.ktorclientapp.ui.screens.viewUser.ranking.RankingViewModel

@Composable
fun MainScreen(idUser: Int, rol: String, alCerrarSesion: () -> Unit, modifier: Modifier = Modifier) {
    // Pestaña que se está viendo: 0 Jugar, 1 Ranking, 2 Perfil, 3 Admin
    var pestana by remember { mutableStateOf(0) }
    val perfilViewModel: PerfilViewModel = viewModel()
    val rankingViewModel: RankingViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()
    val contexto = LocalContext.current
    val imageViewModel: ImageViewModel = viewModel(
        factory = ImageViewModelFactory(contexto.applicationContext as Application)
    )

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
                    onClick = {
                        pestana = 1
                        rankingViewModel.cargarRanking()
                    },
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
                // La pestaña de admin solo la ve quien tiene rol ADMIN
                if (rol == "ADMIN") {
                    NavigationBarItem(
                        selected = pestana == 3,
                        onClick = {
                            pestana = 3
                            adminViewModel.cargarUsuarios()
                        },
                        icon = { Icon(painter = painterResource(R.drawable.ic_admin), contentDescription = "Admin") },
                        label = { Text("Admin") }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Según la pestaña elegida enseñamos una pantalla u otra
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (pestana == 0) {
                JuegoScreen(idUser = idUser)
            }
            if (pestana == 1) {
                RankingScreen(viewModel = rankingViewModel)
            }
            if (pestana == 2) {
                PerfilScreen(
                    idUser = idUser,
                    viewModel = perfilViewModel,
                    imageViewModel = imageViewModel,
                    alCerrarSesion = alCerrarSesion
                )
            }
            if (pestana == 3) {
                AdminScreen(idUser = idUser, viewModel = adminViewModel)
            }
        }
    }
}