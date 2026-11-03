package com.example.ktorclientapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val idUser: Int = 0,
    val rol: String = "GAMER",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val games: Int = 0,
    val wins: Int = 0,
    val foto: String = "",
    val fechaNacimiento: String = ""
)

@Serializable
data class CambioPassword(
    val passwordActual: String = "",
    val passwordNueva: String = ""
)

@Serializable
data class CambioFoto(
    val url: String = ""
)

@Serializable
data class Games(
    val idGames: Int = 0,
    val idUser: Int = 0,
    val intentos: Int = 0,
    val numOculto: Int = 0,
    val activa: Boolean = false,
    val ganado: Boolean = false,
    val pistas: Boolean = false,
    val limitarIntentos: Boolean = false,
    val maxIntentos: Int = 0
)

@Serializable
data class Intento(
    val numero: Int = 0
)

@Serializable
data class RespuestaIntento(
    val resultado: String = "",
    val partida: Games = Games()
)

@Serializable
data class RankingItem(
    val idUser: Int = 0,
    val name: String = "",
    val foto: String = "",
    val jugadas: Int = 0,
    val ganadas: Int = 0,
    val porcentaje: Double = 0.0
)

@Serializable
data class RecuperarPassword(
    val email: String = ""
)
