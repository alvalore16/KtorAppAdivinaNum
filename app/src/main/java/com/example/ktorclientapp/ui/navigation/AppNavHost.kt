package com.example.ktorclientapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ktorclientapp.ui.screens.viewMain.login.LoginScreen
import com.example.ktorclientapp.ui.screens.main.MainScreen
import com.example.ktorclientapp.ui.screens.viewMain.recuperar.RecuperarScreen
import com.example.ktorclientapp.ui.screens.viewMain.register.RegisterScreen

object Rutas {
    const val INICIO_SESION = "inicioSesion"
    const val REGISTRO = "registro"
    const val RECUPERAR = "recuperar"
    const val PRINCIPAL = "principal/{idUser}"

    fun principal(idUser: Int): String {
        return "principal/$idUser"
    }
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
                alIniciarSesion = { idUser ->
                    navController.navigate(Rutas.principal(idUser)) {
                        popUpTo(Rutas.INICIO_SESION) { inclusive = true }
                    }
                },
                irRegistro = {
                    navController.navigate(Rutas.REGISTRO)
                },
                irRecuperar = {
                    navController.navigate(Rutas.RECUPERAR)
                }
            )
        }

        composable(Rutas.REGISTRO) {
            RegisterScreen(
                alRegistrarse = { navController.popBackStack() },
                alVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.RECUPERAR) {
            RecuperarScreen(
                alVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.PRINCIPAL,
            arguments = listOf(navArgument("idUser") { type = NavType.IntType })
        ) { entrada ->
            val argumentos = entrada.arguments
            var idUser = 0
            if (argumentos != null) {
                idUser = argumentos.getInt("idUser")
            }
            MainScreen(
                idUser = idUser,
                alCerrarSesion = {
                    navController.navigate(Rutas.INICIO_SESION) {
                        popUpTo(Rutas.PRINCIPAL) { inclusive = true }
                    }
                }
            )
        }
    }
}