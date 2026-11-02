package com.example.ktorclientapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ktorclientapp.ui.screens.main.MainScreen
import com.example.ktorclientapp.ui.screens.login.LoginScreen
import com.example.ktorclientapp.ui.screens.register.RegisterScreen

object Rutas {
    const val INICIO_SESION = "inicioSesion"
    const val REGISTRO = "registro"
    const val PRINCIPAL = "principal"
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Rutas.INICIO_SESION,
        modifier = modifier
    ) {
        composable(Rutas.INICIO_SESION) {
            LoginScreen(
                alIniciarSesion = {
                    navController.navigate(Rutas.PRINCIPAL) {
                        popUpTo(Rutas.INICIO_SESION) { inclusive = true }
                    }
                },
                irRegistro = {
                    navController.navigate(Rutas.REGISTRO)
                }
            )
        }

        composable(Rutas.REGISTRO) {
            RegisterScreen(
                alRegistrarse = { navController.popBackStack() }
            )
        }

        composable(Rutas.PRINCIPAL) {
            MainScreen(
                alCerrarSesion = {
                    navController.navigate(Rutas.INICIO_SESION) {
                        popUpTo(Rutas.PRINCIPAL) { inclusive = true }
                    }
                }
            )
        }
    }
}