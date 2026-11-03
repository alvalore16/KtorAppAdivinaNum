package com.example.ktorclientapp.ui.screens.perfil

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ktorclientapp.data.repository.ImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 *
 * ImageViewModel.kt coordina la interfaz y el repositorio. Mantiene cuatro estados:
 *
 * imageUri: imagen seleccionada o fotografiada.
 * isUploading: indica si la subida está en curso.
 * message: mensaje de estado para el usuario.
 * uploadedImageUrl: URL devuelta por Cloudinary.
 */
class ImageViewModel(
    private val application: Application,
    private val imageRepository: ImageRepository
) : AndroidViewModel(application) {

    // URI de la foto que se esta mostrando en pantalla.
    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()

    // Indica si la subida esta en curso.
    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    // Mensaje informativo para el alumno.
    private val _message = MutableStateFlow("Todavia no hay ninguna imagen seleccionada")
    val message: StateFlow<String> = _message.asStateFlow()

    // URL que Cloudinary devuelve después de subir la imagen.
    private val _uploadedImageUrl = MutableStateFlow<String?>(null)
    val uploadedImageUrl: StateFlow<String?> = _uploadedImageUrl.asStateFlow()

    fun saveSelectedImage(uri: Uri) {
        _imageUri.value = uri
        _uploadedImageUrl.value = null
        _message.value = "Imagen preparada para subir"
    }

    fun uploadSelectedImage() {
        val uri = _imageUri.value

        if (uri == null) {
            _message.value = "Primero haz una foto o elige una imagen de la galeria"
            return
        }
        /*
        viewModelScope.launch permite ejecutar la operación de forma asíncrona y ligada al ciclo de vida del ViewModel.
        La interfaz no se bloquea mientras se lee el archivo y se realiza la petición de red.
         */
        viewModelScope.launch {
            _isUploading.value = true
            _message.value = "Subiendo imagen a Cloudinary..."

            val result = imageRepository.uploadToCloudinary(
                contentResolver = application.contentResolver,
                imageUri = uri
            )

            if (result.isSuccess) {
                val cloudinaryUrl = result.getOrNull()
                _isUploading.value = false
                _message.value = "Subida completada"
                _uploadedImageUrl.value = cloudinaryUrl
            } else {
                val error = result.exceptionOrNull()
                _isUploading.value = false
                _message.value = "Error: ${error?.message ?: "error desconocido"}"
            }
        }
    }
}

class ImageViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = ImageRepository()
        return ImageViewModel(application, repository) as T
    }
}
