package com.example.ktorclientapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores del modo oscuro (morado claro sobre gris oscuro)
private val DarkColorScheme = darkColorScheme(
    primary = MoradoPastel,
    onPrimary = MoradoOscuroTexto,
    primaryContainer = MoradoApagado,
    onPrimaryContainer = TextoMoradoClaro,
    secondary = GrisClaroTexto,
    onSecondary = FondoGrisOscuro,
    secondaryContainer = GrisTarjetaOscuro,
    onSecondaryContainer = TextoGrisClaro,
    tertiary = MoradoPastel,
    background = FondoGrisOscuro,
    onBackground = TextoGrisClaro,
    surface = FondoGrisOscuro,
    onSurface = TextoGrisClaro,
    surfaceVariant = GrisTarjetaOscuro,
    onSurfaceVariant = GrisClaroTexto,
    outline = GrisBorde
)

// Colores del modo claro (morado sobre gris claro)
private val LightColorScheme = lightColorScheme(
    primary = MoradoClaro,
    onPrimary = Color.White,
    primaryContainer = MoradoSuave,
    onPrimaryContainer = MoradoOscuroTexto,
    secondary = GrisAzulado,
    onSecondary = Color.White,
    secondaryContainer = GrisSuave,
    onSecondaryContainer = TextoGrisOscuro,
    tertiary = MoradoClaro,
    background = FondoGrisClaro,
    onBackground = TextoGrisOscuro,
    surface = FondoGrisClaro,
    onSurface = TextoGrisOscuro,
    surfaceVariant = GrisSuave,
    onSurfaceVariant = GrisAzulado,
    outline = GrisBorde
)

@Composable
fun KtorClientAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Si el móvil está en modo oscuro usamos la paleta oscura, si no la clara
    var colorScheme = LightColorScheme
    if (darkTheme) {
        colorScheme = DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
