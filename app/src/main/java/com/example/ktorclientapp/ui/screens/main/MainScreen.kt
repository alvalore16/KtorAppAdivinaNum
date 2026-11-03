package com.example.ktorclientapp.ui.screens.main

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    // Estado del menú lateral y del menú de tres puntos
    val estadoMenu = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var menuAbierto by remember { mutableStateOf(false) }

    // Cambia de pestaña y carga los datos que necesite, así lo usan la barra de abajo y los menús
    fun irAPestana(numero: Int) {
        pestana = numero
        if (numero == 1) {
            rankingViewModel.cargarRanking()
        }
        if (numero == 2) {
            perfilViewModel.cargarPerfil(idUser)
        }
        if (numero == 3) {
            adminViewModel.cargarUsuarios()
        }
    }

    // El título de arriba cambia según la pestaña
    var titulo = "Jugar"
    if (pestana == 1) {
        titulo = "Ranking"
    }
    if (pestana == 2) {
        titulo = "Perfil"
    }
    if (pestana == 3) {
        titulo = "Administración"
    }

    // ---------- MENÚ LATERAL ----------
    ModalNavigationDrawer(
        drawerState = estadoMenu,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Adivina un número",
                    modifier = Modifier.padding(16.dp)
                )

                NavigationDrawerItem(
                    label = { Text("Jugar") },
                    selected = pestana == 0,
                    onClick = {
                        irAPestana(0)
                        // Cerramos el menú después de elegir
                        scope.launch { estadoMenu.close() }
                    },
                    icon = { Icon(painter = painterResource(R.drawable.ic_jugar), contentDescription = "Jugar") }
                )
                NavigationDrawerItem(
                    label = { Text("Ranking") },
                    selected = pestana == 1,
                    onClick = {
                        irAPestana(1)
                        scope.launch { estadoMenu.close() }
                    },
                    icon = { Icon(painter = painterResource(R.drawable.ic_ranking), contentDescription = "Ranking") }
                )
                NavigationDrawerItem(
                    label = { Text("Perfil") },
                    selected = pestana == 2,
                    onClick = {
                        irAPestana(2)
                        scope.launch { estadoMenu.close() }
                    },
                    icon = { Icon(painter = painterResource(R.drawable.ic_perfil), contentDescription = "Perfil") }
                )
                // Admin solo para quien tiene rol ADMIN
                if (rol == "ADMIN") {
                    NavigationDrawerItem(
                        label = { Text("Administración") },
                        selected = pestana == 3,
                        onClick = {
                            irAPestana(3)
                            scope.launch { estadoMenu.close() }
                        },
                        icon = { Icon(painter = painterResource(R.drawable.ic_admin), contentDescription = "Admin") }
                    )
                }
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = {
                        scope.launch { estadoMenu.close() }
                        alCerrarSesion()
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text(titulo) },
                    // Botón de hamburguesa que abre el menú lateral
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { estadoMenu.open() } }) {
                            Icon(painter = painterResource(R.drawable.ic_menu), contentDescription = "Menú")
                        }
                    },
                    // Botón de tres puntos con su menú desplegable
                    actions = {
                        IconButton(onClick = { menuAbierto = true }) {
                            Icon(painter = painterResource(R.drawable.ic_mas_opciones), contentDescription = "Más opciones")
                        }
                        DropdownMenu(
                            expanded = menuAbierto,
                            onDismissRequest = { menuAbierto = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Mi perfil") },
                                onClick = {
                                    menuAbierto = false
                                    irAPestana(2)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                onClick = {
                                    menuAbierto = false
                                    alCerrarSesion()
                                }
                            )
                        }
                    },
                    // Barra morada con letras e iconos blancos
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = pestana == 0,
                        onClick = { irAPestana(0) },
                        icon = { Icon(painter = painterResource(R.drawable.ic_jugar), contentDescription = "Jugar") },
                        label = { Text("Jugar") }
                    )
                    NavigationBarItem(
                        selected = pestana == 1,
                        onClick = { irAPestana(1) },
                        icon = { Icon(painter = painterResource(R.drawable.ic_ranking), contentDescription = "Ranking") },
                        label = { Text("Ranking") }
                    )
                    NavigationBarItem(
                        selected = pestana == 2,
                        onClick = { irAPestana(2) },
                        icon = { Icon(painter = painterResource(R.drawable.ic_perfil), contentDescription = "Perfil") },
                        label = { Text("Perfil") }
                    )
                    // La pestaña de admin solo la ve quien tiene rol ADMIN
                    if (rol == "ADMIN") {
                        NavigationBarItem(
                            selected = pestana == 3,
                            onClick = { irAPestana(3) },
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
}
