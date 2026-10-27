package com.example.vecinoszaror.repository

import com.example.vecinoszaror.database.dao.UsuarioDao
import com.example.vecinoszaror.model.Usuario
import com.example.vecinoszaror.model.UsuarioConRol
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    fun observarUsuarios(): Flow<List<Usuario>> = usuarioDao.observarTodos()

    fun observarPorRol(rolId: Long): Flow<List<Usuario>> = usuarioDao.observarPorRol(rolId)

    suspend fun obtenerConRol(id: Long): UsuarioConRol? = usuarioDao.obtenerConRol(id)

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

    suspend fun actualizar(usuario: Usuario) = usuarioDao.actualizar(usuario)

    private fun hashClave(clave: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(clave.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
}
