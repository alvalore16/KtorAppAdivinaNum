package com.example.ktorclientapp.data.remote
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClientProvider {

    const val BASE_URL = "http://192.168.209.44:8090"
    // const val BASE_URL_LOCAL = "http://10.0.2.2:8080/posts" // Emulador -> tu backend local en el puerto 8080
    // const val BASE_URL_LOCAL = "http://192.168.1.100:8080" // Dispositivo físico en la misma red Wi-Fi

    val client: HttpClient by lazy {
        HttpClient(CIO) {
            // Serialización JSON automática de/para nuestros data class
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        prettyPrint = true
                    }
                )
            }

            // Logging de peticiones/respuestas, útil para depurar
            install(Logging) {
                level = LogLevel.INFO
            }

            // Timeouts razonables para peticiones de red
            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 15_000
            }
        }
    }
}
