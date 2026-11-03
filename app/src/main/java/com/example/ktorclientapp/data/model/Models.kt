package com.example.ktorclientapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val idUser: Int = 0,
    val rol: String = "gamer",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val games: Int = 0,
    val wins: Int = 0
)
@Serializable
data class CambioPassword(
    val passwordActual: String = "",
    val passwordNueva: String = ""
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
data class UserWithGame(
    val id: Int = 0,
    val name: String = "",
    val games: List<Games> = emptyList()
)