package com.example.ktorclientapp.ui.screens.viewUser.perfil

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.ktorclientapp.data.model.User
import java.io.File

@Composable
fun PerfilScreen(
    idUser: Int,
    viewModel: PerfilViewModel,
    imageViewModel: ImageViewModel,
    alCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados del perfil
    val usuario by viewModel.usuario.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    // Estados de la imagen (Cloudinary)
    val imageUri by imageViewModel.imageUri.collectAsState()
    val isUploading by imageViewModel.isUploading.collectAsState()
    val mensajeImagen by imageViewModel.message.collectAsState()
    val urlSubida by imageViewModel.uploadedImageUrl.collectAsState()

    val contexto = LocalContext.current

    var passwordActual by remember { mutableStateOf("") }
    var passwordNueva by remember { mutableStateOf("") }
    var passwordConfirmacion by remember { mutableStateOf("") }

    // Guardamos la Uri de la foto mientras la cámara está abierta
    var uriCamara by remember { mutableStateOf<Uri?>(null) }

    // ---------- LAUNCHERS ----------
    val galeria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uriElegida ->
            if (uriElegida != null) {
                imageViewModel.saveSelectedImage(uriElegida)
            }
        }
    )

    val camara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { fotoHecha ->
            val uri = uriCamara
            if (fotoHecha && uri != null) {
                imageViewModel.saveSelectedImage(uri)
            }
        }
    )

    val permisoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { permisoConcedido ->
            val uri = uriCamara
            if (permisoConcedido && uri != null) {
                camara.launch(uri)
            }
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Mi perfil", style = MaterialTheme.typography.headlineMedium)

        TarjetaFoto(
            usuario = usuario,
            imageUri = imageUri,
            isUploading = isUploading,
            mensajeImagen = mensajeImagen,
            urlSubida = urlSubida,
            onHacerFoto = {
                val uri = crearUriCamara(contexto)
                uriCamara = uri
                abrirCamaraOPedirPermiso(contexto, uri, camara, permisoCamara)
            },
            onGaleria = { galeria.launch("image/*") },
            onSubir = { imageViewModel.uploadSelectedImage() },
            onGuardar = { url -> viewModel.guardarFoto(idUser, url) }
        )

        TarjetaPassword(
            passwordActual = passwordActual,
            onPasswordActualChange = { passwordActual = it },
            passwordNueva = passwordNueva,
            onPasswordNuevaChange = { passwordNueva = it },
            passwordConfirmacion = passwordConfirmacion,
            onPasswordConfirmacionChange = { passwordConfirmacion = it },
            onCambiar = {
                viewModel.cambiarPassword(idUser, passwordActual, passwordNueva, passwordConfirmacion)
                // Vaciamos los campos para que no se queden a la vista
                passwordActual = ""
                passwordNueva = ""
                passwordConfirmacion = ""
            }
        )

        // Avisos del perfil (contraseña, foto...)
        if (mensaje != "") {
            Text(text = mensaje)
        }

        Button(
            onClick = alCerrarSesion,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}

// ---------- TARJETA FOTO Y DATOS ----------
@Composable
private fun TarjetaFoto(
    usuario: User?,
    imageUri: Uri?,
    isUploading: Boolean,
    mensajeImagen: String,
    urlSubida: String?,
    onHacerFoto: () -> Unit,
    onGaleria: () -> Unit,
    onSubir: () -> Unit,
    onGuardar: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (usuario == null) {
                Text("Cargando perfil...")
            } else {
                // Si ha elegido una imagen nueva se ve esa, si no la que tiene guardada
                if (imageUri != null) {
                    FotoPerfil(imageUri)
                } else if (usuario.foto.isNotBlank()) {
                    FotoPerfil(usuario.foto)
                } else {
                    Text("Sin foto de perfil")
                }

                Text(text = usuario.name, style = MaterialTheme.typography.titleLarge)
                Text(text = usuario.email)
                Text(text = "Rol: ${usuario.rol}")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = onHacerFoto, modifier = Modifier.weight(1f)) {
                        Text("Hacer foto")
                    }
                    Button(onClick = onGaleria, modifier = Modifier.weight(1f)) {
                        Text("Galería")
                    }
                }

                // Primero subimos la imagen a Cloudinary
                Button(
                    onClick = onSubir,
                    enabled = imageUri != null && !isUploading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Subir foto")
                    }
                }

                Text(text = mensajeImagen, style = MaterialTheme.typography.bodySmall)

                // Cuando Cloudinary nos da la URL ya la podemos guardar en el servidor
                if (urlSubida != null && urlSubida != usuario.foto) {
                    Button(
                        onClick = { onGuardar(urlSubida) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar como foto de perfil")
                    }
                }
            }
        }
    }
}

// ---------- TARJETA CAMBIAR CONTRASEÑA ----------
@Composable
private fun TarjetaPassword(
    passwordActual: String,
    onPasswordActualChange: (String) -> Unit,
    passwordNueva: String,
    onPasswordNuevaChange: (String) -> Unit,
    passwordConfirmacion: String,
    onPasswordConfirmacionChange: (String) -> Unit,
    onCambiar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Cambiar contraseña", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = passwordActual,
                onValueChange = onPasswordActualChange,
                label = { Text("Contraseña actual") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = passwordNueva,
                onValueChange = onPasswordNuevaChange,
                label = { Text("Contraseña nueva") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = passwordConfirmacion,
                onValueChange = onPasswordConfirmacionChange,
                label = { Text("Repite la contraseña nueva") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = onCambiar, modifier = Modifier.fillMaxWidth()) {
                Text("Cambiar contraseña")
            }
        }
    }
}

// Foto redonda. Vale tanto una Uri del móvil como la URL de Cloudinary
@Composable
private fun FotoPerfil(foto: Any?) {
    AsyncImage(
        model = foto,
        contentDescription = "Foto de perfil",
        modifier = Modifier.size(120.dp).clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

// Crea un archivo temporal para la foto y devuelve su Uri (con FileProvider)
private fun crearUriCamara(contexto: Context): Uri {
    val carpeta = File(contexto.cacheDir, "images")
    carpeta.mkdirs()

    val archivo = File.createTempFile("foto_", ".jpg", carpeta)
    val authority = contexto.packageName + ".fileprovider"

    return FileProvider.getUriForFile(contexto, authority, archivo)
}

// Si ya tenemos permiso abrimos la cámara, si no lo pedimos
private fun abrirCamaraOPedirPermiso(
    contexto: Context,
    uri: Uri,
    camara: ActivityResultLauncher<Uri>,
    permisoCamara: ActivityResultLauncher<String>
) {
    val permiso = Manifest.permission.CAMERA
    val permisoConcedido = ContextCompat.checkSelfPermission(
        contexto,
        permiso
    ) == PackageManager.PERMISSION_GRANTED

    if (permisoConcedido) {
        camara.launch(uri)
    } else {
        permisoCamara.launch(permiso)
    }
}
