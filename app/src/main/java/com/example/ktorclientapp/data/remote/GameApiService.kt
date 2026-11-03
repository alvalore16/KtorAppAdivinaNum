package com.example.ktorclientapp.data.remote

import com.example.ktorclientapp.data.model.CambioFoto
import com.example.ktorclientapp.data.model.CambioPassword
import com.example.ktorclientapp.data.model.CambioRol
import com.example.ktorclientapp.data.model.Games
import com.example.ktorclientapp.data.model.Intento
import com.example.ktorclientapp.data.model.RankingItem
import com.example.ktorclientapp.data.model.RecuperarPassword
import com.example.ktorclientapp.data.model.RespuestaIntento
import com.example.ktorclientapp.data.model.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GameApiService(
    private val client: HttpClient = KtorClientProvider.client,
    private val baseUrl: String = KtorClientProvider.BASE_URL
) {
    suspend fun getRanking(): List<RankingItem> = client.get("$baseUrl/ranking").body()

    suspend fun getUsers(): List<User> = client.get("$baseUrl/usuarios").body()

    suspend fun getUserById(id: Int): User = client.get("$baseUrl/usuarios/$id").body()

    suspend fun createUser(user: User): User =
        client.post("$baseUrl/usuarios") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.body()

    suspend fun crearPartida(partida: Games): Games =
        client.post("$baseUrl/partidas") {
            contentType(ContentType.Application.Json)
            setBody(partida)
        }.body()

    suspend fun getPartidaActiva(idUser: Int): Games =
        client.get("$baseUrl/usuarios/$idUser/partida-activa").body()

    suspend fun enviarIntento(idPartida: Int, numero: Int): RespuestaIntento =
        client.post("$baseUrl/partidas/$idPartida/intentos") {
            contentType(ContentType.Application.Json)
            setBody(Intento(numero))
        }.body()

    suspend fun cambiarPassword(idUser: Int, cambio: CambioPassword) {
        client.put("$baseUrl/usuarios/$idUser/password") {
            contentType(ContentType.Application.Json)
            setBody(cambio)
        }
    }

    suspend fun cambiarFoto(idUser: Int, cambio: CambioFoto) {
        client.put("$baseUrl/usuarios/$idUser/foto") {
            contentType(ContentType.Application.Json)
            setBody(cambio)
        }
    }

    suspend fun recuperarPassword(email: String) {
        client.post("$baseUrl/recuperar-password") {
            contentType(ContentType.Application.Json)
            setBody(RecuperarPassword(email))
        }
    }

    // Crear usuario desde el panel de admin (el rol lo elige el admin)
    suspend fun crearUsuarioAdmin(user: User) {
        client.post("$baseUrl/admin/usuarios") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
    }

    // Cambiar nombre, correo y rol
    suspend fun actualizarUsuario(idUser: Int, user: User) {
        client.put("$baseUrl/usuarios/$idUser") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
    }

    // Solo cambia el rol
    suspend fun cambiarRol(idUser: Int, cambio: CambioRol) {
        client.put("$baseUrl/usuarios/$idUser/rol") {
            contentType(ContentType.Application.Json)
            setBody(cambio)
        }
    }

    // El servidor también borra sus partidas
    suspend fun eliminarUsuario(idUser: Int) {
        client.delete("$baseUrl/usuarios/$idUser")
    }
}
