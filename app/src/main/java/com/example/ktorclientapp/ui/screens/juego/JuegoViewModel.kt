package com.example.ktorclientapp.ui.screens.juego

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

class JuegoViewModel(private val repositorio: GameRepository = GameRepository()) : ViewModel() {

    // null = no hay partida en pantalla
    private val _partida = MutableStateFlow<Games?>(null)
    val partida: StateFlow<Games?> = _partida.asStateFlow()

    // Pistas, resultados y errores se muestran en un único texto
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
                    // Ya tiene una partida sin terminar: se continúa con ella
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

        val numero = texto.trim().toIntOrNull()
        if (numero == null) {
            _mensaje.value = "Introduce un número válido"
            return
        }

        viewModelScope.launch {
            try {
                val respuesta = repositorio.enviarIntento(partidaActual.idGames, numero)
                _partida.value = respuesta.partida

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

    // Si la partida no estaba terminada, sigue guardada en el servidor
    fun salirDePartida() {
        _partida.value = null
        _mensaje.value = ""
    }
}