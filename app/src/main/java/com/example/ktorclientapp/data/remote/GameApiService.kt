package com.example.ktorclientapp.data.remote

import com.example.ktorclientapp.data.model.Games
import com.example.ktorclientapp.data.model.User
import com.example.ktorclientapp.data.model.UserWithGame
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GameApiService(
    private val client: HttpClient = KtorClientProvider.client,
    private val baseUrl: String = KtorClientProvider.BASE_URL
) {
    suspend fun getUsers(): List<User> = client.get("$baseUrl/usuarios").body()

    suspend fun getUserById(id: Int): User = client.get("$baseUrl/usuarios/$id").body()

    suspend fun getUserWithGames(id: Int): UserWithGame =
        client.get("$baseUrl/usuarios/$id/games").body()

    suspend fun createUser(user: User): User =
        client.post("$baseUrl/usuarios") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.body()

    suspend fun getGames(): List<Games> = client.get("$baseUrl/games").body()

    suspend fun getGameById(id: Int): Games = client.get("$baseUrl/games/$id").body()

    suspend fun createGame(game: Games): Games =
        client.post("$baseUrl/games") {
            contentType(ContentType.Application.Json)
            setBody(game)
        }.body()
}