package com.example.ktorclientapp.ui.screens.viewUser.juego

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktorclientapp.data.model.Games
import com.example.ktorclientapp.data.repository.GameRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JuegoViewModel(
    private val repositorio: GameRepository = GameRepository()
) : ViewModel() {

    // La partida que se está jugando. Si es null, no hay ninguna en pantalla
    private val _partida = MutableStateFlow<Games?>(null)
    val partida: StateFlow<Games?> = _partida.asStateFlow()

    // Aquí van las pistas, los resultados y los errores, todo en un solo texto
    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    fun empezarPartida(idUser: Int, pistas: Boolean, limitarIntentos: Boolean, maxIntentos: Int) {
        viewModelScope.launch {
            try {
                val nueva = Games(
                    idUser = idUser,
                    pistas = pistas,
                    limitarIntentos = limitarIntentos,
                    maxIntentos = maxIntentos
                )
                _partida.value = repositorio.crearPartida(nueva)
                _mensaje.value = "Adivina el número"
            } catch (e: ClientRequestException) {
                if (e.response.status == HttpStatusCode.Conflict) {
                    // Ya tenía una partida sin terminar, seguimos con esa
                    continuarPartida(idUser)
                } else {
                    _mensaje.value = e.response.bodyAsText()
                }
            } catch (e: Exception) {
                _mensaje.value = "No se pudo conectar con el servidor"
            }
        }
    }

    private fun continuarPartida(idUser: Int) {
        viewModelScope.launch {
            try {
                _partida.value = repositorio.getPartidaActiva(idUser)
                _mensaje.value = "Tenías una partida sin terminar, sigue intentándolo"
            } catch (e: Exception) {
                _mensaje.value = "No se pudo recuperar la partida"
            }
        }
    }

    fun enviarIntento(texto: String) {
        val partidaActual = _partida.value
        if (partidaActual == null) {
            return
        }

        // Comprobamos que lo que ha escrito es un número
        val numero = texto.trim().toIntOrNull()
        if (numero == null) {
            _mensaje.value = "Introduce un número válido"
            return
        }

        viewModelScope.launch {
            try {
                val respuesta = repositorio.enviarIntento(partidaActual.idGames, numero)
                _partida.value = respuesta.partida

                // El servidor es el que sabe el número, nosotros solo mostramos lo que dice
                when (respuesta.resultado) {
                    "ACERTADO" -> _mensaje.value = "¡Has acertado!"
                    "PERDIDO" -> _mensaje.value = "Sin intentos. El número era ${respuesta.partida.numOculto}"
                    "MAYOR" -> _mensaje.value = "El número secreto es mayor que $numero"
                    "MENOR" -> _mensaje.value = "El número secreto es menor que $numero"
                    else -> _mensaje.value = "No has acertado, sigue intentándolo"
                }
            } catch (e: Exception) {
                _mensaje.value = "No se pudo enviar el intento"
            }
        }
    }

    // Si la partida no estaba acabada, sigue guardada en el servidor
    fun salirDePartida() {
        _partida.value = null
        _mensaje.value = ""
    }
}
