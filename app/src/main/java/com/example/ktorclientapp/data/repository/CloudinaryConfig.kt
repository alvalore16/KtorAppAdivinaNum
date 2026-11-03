package com.example.ktorclientapp.data.repository

/**
 * Configuracion didactica para una subida unsigned.
 *
 * Crea un upload preset unsigned en Cloudinary y sustituye estos dos valores.
 * Una API secret nunca debe incluirse en una aplicacion Android.
 */
object CloudinaryConfig {
    const val CLOUD_NAME = "zxy1dfo0"
    const val UPLOAD_PRESET = "android_compose"
    const val FOLDER = "app/perfiles"
}
