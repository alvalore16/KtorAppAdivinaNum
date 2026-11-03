package com.example.ktorclientapp.data.repository

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ImageRepository {

    suspend fun uploadToCloudinary(
        contentResolver: ContentResolver,
        imageUri: Uri
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            // 1. Preparamos los datos necesarios para la petición.
            val boundary = createMultipartBoundary()
            val connection = openCloudinaryConnection(boundary)

            try {
                // 2. Escribimos el formulario multipart en la conexión.
                writeMultipartBody(
                    connection = connection,
                    boundary = boundary,
                    contentResolver = contentResolver,
                    imageUri = imageUri
                )

                // 3. Leemos la respuesta de Cloudinary.
                val response = readCloudinaryResponse(connection)

                // 4. Extraemos la URL que devolveremos a la aplicación.
                extractSecureUrl(response)
            } finally {
                // Se cierra la conexión tanto si todo va bien como si hay un error.
                connection.disconnect()
            }
        }
    }

    /** Crea un separador que distingue las partes del formulario. */
    private fun createMultipartBoundary(): String {
        return "----AndroidCloudinaryBoundary${System.currentTimeMillis()}"
    }

    /** Abre y configura la conexión HTTP, pero todavía no envía ningún dato. */
    private fun openCloudinaryConnection(boundary: String): HttpURLConnection {
        val endpoint =
            "https://api.cloudinary.com/v1_1/${CloudinaryConfig.CLOUD_NAME}/image/upload"

        val url = URL(endpoint)
        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.doInput = true
        connection.useCaches = false
        connection.setRequestProperty(
            "Content-Type",
            "multipart/form-data; boundary=$boundary"
        )

        return connection
    }

    /** Escribe los campos de texto y los bytes de la imagen. */
    private fun writeMultipartBody(
        connection: HttpURLConnection,
        boundary: String,
        contentResolver: ContentResolver,
        imageUri: Uri
    ) {
        // outputStream representa el cuerpo de la petición POST.
        DataOutputStream(connection.outputStream).use { output ->
            writeTextPart(
                output = output,
                boundary = boundary,
                name = "upload_preset",
                value = CloudinaryConfig.UPLOAD_PRESET
            )
            writeTextPart(
                output = output,
                boundary = boundary,
                name = "folder",
                value = CloudinaryConfig.FOLDER
            )
            writeFilePart(
                output = output,
                boundary = boundary,
                contentResolver = contentResolver,
                imageUri = imageUri
            )

            // Indica que no quedan más partes del formulario.
            output.writeBytes("--$boundary--\r\n")
        }
    }

    /** Escribe un campo normal del formulario, como upload_preset o folder. */
    private fun writeTextPart(
        output: DataOutputStream,
        boundary: String,
        name: String,
        value: String
    ) {
        output.writeBytes("--$boundary\r\n")
        output.writeBytes("Content-Disposition: form-data; name=\"$name\"\r\n\r\n")
        output.writeBytes("$value\r\n")
    }

    /** Escribe la parte file y copia en ella los bytes de la Uri. */
    private fun writeFilePart(
        output: DataOutputStream,
        boundary: String,
        contentResolver: ContentResolver,
        imageUri: Uri
    ) {
        output.writeBytes("--$boundary\r\n")
        output.writeBytes(
            "Content-Disposition: form-data; name=\"file\"; filename=\"foto.jpg\"\r\n"
        )
        output.writeBytes("Content-Type: image/jpeg\r\n\r\n")

        val input = contentResolver.openInputStream(imageUri)
            ?: error("No se pudo leer la imagen seleccionada")

        input.use { imageInput ->
            imageInput.copyTo(output)
        }

        output.writeBytes("\r\n")
    }

    /** Lee el cuerpo de respuesta correcto o el cuerpo de error. */
    private fun readCloudinaryResponse(connection: HttpURLConnection): String {
        val responseCode = connection.responseCode
        val responseStream = if (responseCode in 200..299) {
            connection.inputStream
        } else {
            connection.errorStream
        }

        val responseText = responseStream
            ?.bufferedReader()
            ?.use { reader -> reader.readText() }
            .orEmpty()

        if (responseCode !in 200..299) {
            error("Cloudinary ($responseCode): $responseText")
        }

        return responseText
    }

    /** Obtiene secure_url del JSON que devuelve Cloudinary. */
    private fun extractSecureUrl(responseText: String): String {
        val responseJson = JSONObject(responseText)
        val secureUrl = responseJson.optString("secure_url")

        if (secureUrl.isBlank()) {
            error("Cloudinary no devolvio la URL de la imagen")
        }

        return secureUrl
    }
}
