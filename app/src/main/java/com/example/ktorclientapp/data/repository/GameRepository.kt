package com.example.ktorclientapp.data.repository

import com.example.ktorclientapp.data.model.CambioFoto
import com.example.ktorclientapp.data.model.CambioPassword
import com.example.ktorclientapp.data.model.Games
import com.example.ktorclientapp.data.model.RankingItem
import com.example.ktorclientapp.data.model.RespuestaIntento
import com.example.ktorclientapp.data.model.User
import com.example.ktorclientapp.data.remote.GameApiService

class GameRepository(
    private val apiService: GameApiService = GameApiService()
) {
    suspend fun getRanking(): List<RankingItem> = apiService.getRanking()

    suspend fun getUsers(): List<User> = apiService.getUsers()

    suspend fun getUserById(id: Int): User = apiService.getUserById(id)

    suspend fun createUser(user: User): User = apiService.createUser(user)

    suspend fun crearPartida(partida: Games): Games = apiService.crearPartida(partida)

    suspend fun getPartidaActiva(idUser: Int): Games = apiService.getPartidaActiva(idUser)

    suspend fun enviarIntento(idPartida: Int, numero: Int): RespuestaIntento = apiService.enviarIntento(idPartida, numero)

    suspend fun cambiarPassword(idUser: Int, cambio: CambioPassword) = apiService.cambiarPassword(idUser, cambio)

    suspend fun cambiarFoto(idUser: Int, cambio: CambioFoto) = apiService.cambiarFoto(idUser, cambio)

    suspend fun recuperarPassword(email: String) = apiService.recuperarPassword(email)
}
