package com.example.ktorclientapp.data.repository

import com.example.ktorclientapp.data.model.Games
import com.example.ktorclientapp.data.model.User
import com.example.ktorclientapp.data.model.UserWithGame
import com.example.ktorclientapp.data.remote.GameApiService

class GameRepository(
    private val apiService: GameApiService = GameApiService()
) {
    suspend fun getUsers(): List<User> = apiService.getUsers()

    suspend fun getUserById(id: Int): User = apiService.getUserById(id)

    suspend fun getUserWithGames(id: Int): UserWithGame = apiService.getUserWithGames(id)

    suspend fun createUser(user: User): User = apiService.createUser(user)

    suspend fun getGames(): List<Games> = apiService.getGames()

    suspend fun getGameById(id: Int): Games = apiService.getGameById(id)

    suspend fun createGame(game: Games): Games = apiService.createGame(game)
}