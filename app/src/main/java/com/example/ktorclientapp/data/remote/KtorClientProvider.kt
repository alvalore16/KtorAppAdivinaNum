package com.example.ktorclientapp.data.remote
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Punto único de configuración del cliente Ktor.
 *
 * Aquí se define tanto la URL base de la API pública de pruebas
 * (JSONPlaceholder) como, comentada, la configuración necesaria para
 * apuntar a una API propia servida en localhost (por ejemplo durante
 * el desarrollo con un backend local).
 */
object KtorClientProvider {

    /** API pública usada por defecto para las pruebas de este proyecto. */
    const val BASE_URL = "http://10.0.2.2:8090"
//        "https://jsonplaceholder.typicode.com"

    /**
     * URL para conectarse a una API alojada en local.
     *
     * - Si ejecutas la app en un EMULADOR Android, "localhost" de tu PC
     *   se mapea a través de la IP especial 10.0.2.2.
     * - Si ejecutas en un DISPOSITIVO FÍSICO, deberás usar la IP de tu
     *   máquina dentro de la red local (ej: "http://192.168.1.100:8080").
     * - Si tu servidor local usa HTTP (sin TLS), recuerda añadir
     *   android:usesCleartextTraffic="true" en el AndroidManifest.xml,
     *   ya que Android bloquea tráfico HTTP por defecto a partir de API 28.
     */
//    const val BASE_URL_LOCAL = "http://10.0.2.2:8080/posts" // Emulador -> tu backend local en el puerto 8080
    // const val BASE_URL_LOCAL = "http://192.168.1.100:8080" // Dispositivo físico en la misma red Wi-Fi

    /**
     * Instancia única (singleton) del HttpClient de Ktor.
     * Usa el engine CIO (válido tanto en Android como en Desktop/JVM).
     */
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

            // Para conectar con la API local que use HTTPS con certificado
            // autofirmado en desarrollo, podrías necesitar configurar el
            // engine para confiar en dicho certificado. Ejemplo (NO usar
            // en producción, solo para depurar en local):
            //
            // engine {
            //     https {
            //         trustManager = /* TrustManager que acepte el certificado local */
            //     }
            // }
        }
    }
}
