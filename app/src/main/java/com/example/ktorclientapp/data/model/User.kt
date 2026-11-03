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
data class CambioRol(
    val rol: String = ""
)

@Serializable
data class RecuperarPassword(
    val email: String = ""
)

@Serializable
data class Login(
    val email: String = "",
    val password: String = ""
)

@Serializable
data class RespuestaLogin(
    val idUser: Int = 0,
    val rol: String = ""
)
