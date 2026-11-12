package com.example.vecinoszaror.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.vecinoszaror.database.dao.UsuarioDao
import com.example.vecinoszaror.model.Usuario
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    fun observarUsuarios(): Flow<List<Usuario>> = usuarioDao.observarTodos()

    /** Devuelve el usuario, o null si la clave es incorrecta o está inactivo. */
    suspend fun iniciarSesion(email: String, clave: String): Usuario? =
        usuarioDao.autenticar(email.trim().lowercase(), hashClave(clave))

    /** Devuelve el id, o null si el email ya existe. */
    suspend fun registrar(
        nombre: String,
        apellido: String,
        email: String,
        telefono: String?,
        clave: String,
        rolId: Long
    ): Long? = usuarioDao.insertar(
        Usuario(
            nombre = nombre.trim(),
            apellido = apellido.trim(),
            email = email.trim().lowercase(),
            telefono = telefono?.trim(),
            claveHash = hashClave(clave),
            rolId = rolId
        )
    ).takeIf { it != -1L }

    suspend fun obtenerPorId(id: Long): Usuario? = usuarioDao.obtenerPorId(id)

    suspend fun obtenerPorEmail(email: String): Usuario? =
        usuarioDao.obtenerPorEmail(email.trim().lowercase())

    suspend fun obtenerActivosConPermiso(codigoPermiso: String): List<Usuario> =
        usuarioDao.obtenerActivosConPermiso(codigoPermiso)

    suspend fun obtenerActivosPorRoles(nombresRol: List<String>): List<Usuario> =
        usuarioDao.obtenerActivosPorRoles(nombresRol)

    /**
     * Guarda los cambios de un usuario. Si [nuevaClave] no es null, también cambia su clave.
     * Devuelve false si el correo ya lo usa otra persona.
     */
    suspend fun editar(usuario: Usuario, nuevaClave: String?): Boolean {
        val actualizado = usuario.copy(
            nombre = usuario.nombre.trim(),
            apellido = usuario.apellido.trim(),
            email = usuario.email.trim().lowercase(),
            telefono = usuario.telefono?.trim(),
            claveHash = if (nuevaClave != null) hashClave(nuevaClave) else usuario.claveHash
        )
        return try {
            usuarioDao.actualizar(actualizado)
            true
        } catch (e: SQLiteConstraintException) {
            // El correo es único
            false
        }
    }

    /** Indica si la [clave] coincide con la guardada en la base para ese usuario. */
    suspend fun claveCorrecta(usuarioId: Long, clave: String): Boolean {
        val usuario = usuarioDao.obtenerPorId(usuarioId) ?: return false
        return usuario.claveHash == hashClave(clave)
    }

    /**
     * Cambia la clave de un usuario si [claveActual] es la correcta.
     * Devuelve false si la clave actual no coincide.
     */
    suspend fun cambiarClave(usuarioId: Long, claveActual: String, claveNueva: String): Boolean {
        val usuario = usuarioDao.obtenerPorId(usuarioId) ?: return false
        if (usuario.claveHash != hashClave(claveActual)) return false
        usuarioDao.actualizar(usuario.copy(claveHash = hashClave(claveNueva)))
        return true
    }

    /**
     * Guarda los datos personales de un usuario (no toca correo, clave, rol ni estado).
     * Devuelve el usuario ya actualizado, o null si no existe.
     */
    suspend fun actualizarPerfil(usuarioId: Long, nombre: String, apellido: String, telefono: String?): Usuario? {
        val usuario = usuarioDao.obtenerPorId(usuarioId) ?: return null
        val actualizado = usuario.copy(
            nombre = nombre.trim(),
            apellido = apellido.trim(),
            telefono = telefono?.trim()?.takeIf { it.isNotEmpty() }
        )
        usuarioDao.actualizar(actualizado)
        return actualizado
    }

    private fun hashClave(clave: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(clave.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
}
