package com.example.ktorclientapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Ranking(
    val idUser: Int = 0,
    val name: String = "",
    val foto: String = "",
    val jugadas: Int = 0,
    val ganadas: Int = 0,
    val porcentaje: Double = 0.0
)
