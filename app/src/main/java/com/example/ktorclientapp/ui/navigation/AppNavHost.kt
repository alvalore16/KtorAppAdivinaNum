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

// Aquí tenemos todas las rutas de la app juntas
object Rutas {
    const val INICIO_SESION = "inicioSesion"
    const val REGISTRO = "registro"
    const val RECUPERAR = "recuperar"
    const val PRINCIPAL = "principal/{idUser}/{rol}"

    // La ruta principal lleva el id y el rol dentro
    fun principal(idUser: Int, rol: String): String {
        return "principal/$idUser/$rol"
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
                alIniciarSesion = { idUser, rol ->
                    // Al entrar borramos el login de la pila para que atrás no vuelva a él
                    navController.navigate(Rutas.principal(idUser, rol)) {
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
            arguments = listOf(
                navArgument("idUser") { type = NavType.IntType },
                navArgument("rol") { type = NavType.StringType }
            )
        ) { entrada ->
            // Sacamos el id y el rol de la ruta
            val argumentos = entrada.arguments
            var idUser = 0
            // Si por lo que sea no llega el rol, lo tratamos como un GAMER normal
            var rol = "GAMER"
            if (argumentos != null) {
                idUser = argumentos.getInt("idUser")
                val rolRecibido = argumentos.getString("rol")
                if (rolRecibido != null) {
                    rol = rolRecibido
                }
            }
            MainScreen(
                idUser = idUser,
                rol = rol,
                alCerrarSesion = {
                    navController.navigate(Rutas.INICIO_SESION) {
                        popUpTo(Rutas.PRINCIPAL) { inclusive = true }
                    }
                }
            )
        }
    }
}