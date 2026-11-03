package com.example.vecinoszaror.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Carpeta privada de la app (files/comprobantes/), debe coincidir con res/xml/file_paths.xml
private const val CARPETA_COMPROBANTES = "comprobantes"

private fun nuevoArchivo(context: Context): File {
    val carpeta = File(context.filesDir, CARPETA_COMPROBANTES).apply { mkdirs() }
    return File(carpeta, "comprobante_${System.currentTimeMillis()}.jpg")
}
// Debe coincidir con la autoridad en el AndroidManifest
private fun dirigir(context: Context, archivo: File): Uri =
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)


/** Crea un archivo vacío para que la cámara guarde ahí la foto, y devuelve su dirección. */
fun crearUriParaFoto(context: Context): Uri = dirigir(context, nuevoArchivo(context))

/**
 * Copia una imagen elegida de la galería a la carpeta de la app y devuelve la nueva dirección.
 * Devuelve null si no se pudo copiar.
 */
suspend fun copiarAComprobantes(context: Context, origen: Uri): Uri? = withContext(Dispatchers.IO) {
    try {
        val destino = nuevoArchivo(context)
        val entrada = context.contentResolver.openInputStream(origen) ?: return@withContext null
        entrada.use { lectura ->
            destino.outputStream().use { escritura -> lectura.copyTo(escritura) }
        }
        dirigir(context, destino)
    } catch (e: Exception) {
        null
    }
}
