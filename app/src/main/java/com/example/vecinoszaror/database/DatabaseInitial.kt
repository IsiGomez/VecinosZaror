package com.example.vecinoszaror.database

import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.vecinoszaror.model.Permiso
import com.example.vecinoszaror.model.Recinto
import com.example.vecinoszaror.model.Rol

/** Carga roles, permisos, rol-permiso, recintos y un usuario de prueba por rol. */
object DatabaseInitial {

    // SHA-256 de "demo1234"
    private const val CLAVE_DEMO_HASH =
        "0ead2060b65992dca4769af601a1b3a35ef38cfad2c2c465bb160ea764157c5d"

    private data class UsuarioDemo(
        val nombre: String,
        val apellido: String,
        val email: String,
        val rol: String
    )

    fun poblar(db: SupportSQLiteDatabase) {
        insertarRoles(db)
        insertarPermisos(db)
        asignarPermisosARoles(db)
        insertarRecintos(db)
        insertarUsuariosDemo(db)
    }

    private fun insertarRoles(db: SupportSQLiteDatabase) {
        val roles = listOf(
            Rol(nombre = "VECINO", descripcion = "Vecino/a: solicita arriendos y consulta información financiera"),
            Rol(nombre = "SECRETARIA", descripcion = "Secretaría: verifica disponibilidad y gestiona reservas"),
            Rol(nombre = "TESORERIA", descripcion = "Tesorería: registra pagos y gastos, prepara informes"),
            Rol(nombre = "DIRECTIVA", descripcion = "Directiva: seguimiento de gastos y recursos, publica informes")
        )

        roles.forEach { r ->
            db.execSQL(
                "INSERT INTO rol (nombre, descripcion) VALUES (?, ?)",
                arrayOf<Any>(r.nombre, r.descripcion)
            )
        }
    }

    private fun insertarPermisos(db: SupportSQLiteDatabase) {
        val permisos = listOf(
            Permiso(
                codigo = "RESERVA_SOLICITAR", descripcion = "Solicitar el arriendo de un recinto",
                modulo = "RESERVAS"),
            Permiso(
                codigo = "RESERVA_VER_PROPIAS", descripcion = "Ver las reservas propias",
                modulo = "RESERVAS"),
            Permiso(
                codigo = "RESERVA_CANCELAR_PROPIA", descripcion = "Cancelar una reserva propia",
                modulo = "RESERVAS"),
            Permiso(
                codigo = "RESERVA_VER_TODAS", descripcion = "Ver todas las reservas",
                modulo = "RESERVAS"),
            Permiso(
                codigo = "RESERVA_VERIFICAR_DISPONIBILIDAD", descripcion = "Verificar disponibilidad de fechas y horarios",
                modulo = "RESERVAS"),
            Permiso(
                codigo = "RESERVA_GESTIONAR", descripcion = "Confirmar, rechazar o cancelar reservas",
                modulo = "RESERVAS"),
            Permiso(
                codigo = "PAGO_REGISTRAR", descripcion = "Registrar pagos de arriendo",
                modulo = "FINANZAS"),
            Permiso(
                codigo = "GASTO_REGISTRAR", descripcion = "Registrar gastos y consumos básicos",
                modulo = "FINANZAS"),
            Permiso(
                codigo = "FINANZAS_VER_DETALLE", descripcion = "Ver el detalle de ingresos y gastos",
                modulo = "FINANZAS"),
            Permiso(
                codigo = "INFORME_GENERAR", descripcion = "Generar informes y rendiciones de cuentas",
                modulo = "INFORMES"),
            Permiso(
                codigo = "INFORME_PUBLICAR", descripcion = "Publicar informes para los vecinos",
                modulo = "INFORMES"),
            Permiso(
                codigo = "INFORME_VER_PUBLICADOS", descripcion = "Ver informes publicados",
                modulo = "INFORMES"),
            Permiso(
                codigo = "USUARIO_GESTIONAR", descripcion = "Administrar usuarios",
                modulo = "ADMINISTRACION"),
            Permiso(
                codigo = "ROL_PERMISO_GESTIONAR", descripcion = "Administrar roles y permisos",
                modulo = "ADMINISTRACION"),
            Permiso(
                codigo = "RECINTO_GESTIONAR", descripcion = "Administrar recintos y tarifas",
                modulo = "ADMINISTRACION")
        )

        permisos.forEach { p ->
            db.execSQL(
                "INSERT INTO permiso (codigo, descripcion, modulo) VALUES (?, ?, ?)",
                arrayOf<Any>(p.codigo, p.descripcion, p.modulo)
            )
        }
    }

    private fun asignarPermisosARoles(db: SupportSQLiteDatabase) {
        val matriz = mapOf(
            "VECINO" to listOf(
                "RESERVA_SOLICITAR", "RESERVA_VER_PROPIAS", "RESERVA_CANCELAR_PROPIA",
                "INFORME_VER_PUBLICADOS"
            ),
            "SECRETARIA" to listOf(
                "RESERVA_SOLICITAR", "RESERVA_VER_PROPIAS", "RESERVA_CANCELAR_PROPIA",
                "RESERVA_VER_TODAS", "RESERVA_VERIFICAR_DISPONIBILIDAD", "RESERVA_GESTIONAR",
                "INFORME_VER_PUBLICADOS"
            ),
            "TESORERIA" to listOf(
                "RESERVA_VER_TODAS", "PAGO_REGISTRAR", "GASTO_REGISTRAR",
                "FINANZAS_VER_DETALLE", "INFORME_GENERAR", "INFORME_VER_PUBLICADOS"
            ),
            "DIRECTIVA" to listOf(
                "RESERVA_VER_TODAS", "RESERVA_VERIFICAR_DISPONIBILIDAD", "RESERVA_GESTIONAR",
                "FINANZAS_VER_DETALLE", "INFORME_PUBLICAR", "INFORME_VER_PUBLICADOS",
                "USUARIO_GESTIONAR", "ROL_PERMISO_GESTIONAR", "RECINTO_GESTIONAR"
            )
        )

        matriz.forEach { (rol, codigos) ->
            codigos.forEach { codigo ->
                db.execSQL(
                    "INSERT INTO rol_permiso (rolId, permisoId) " +
                    "SELECT r.id, p.id FROM rol r, permiso p WHERE r.nombre = ? AND p.codigo = ?",
                    arrayOf<Any>(rol, codigo)
                )
            }
        }
    }

    private fun insertarRecintos(db: SupportSQLiteDatabase) {
        val recintos = listOf(
            Recinto(nombre = "Salón Multiuso", descripcion = "Salón principal para eventos y celebraciones",
                    capacidadPersonas = 80, tarifaBase = 45000),
            Recinto(nombre = "Sala de Reuniones", descripcion = "Sala para reuniones y talleres pequeños",
                    capacidadPersonas = 20, tarifaBase = 15000),
            Recinto(nombre = "Patio Techado", descripcion = "Espacio techado para actividades al aire libre",
                    capacidadPersonas = 50, tarifaBase = 25000)
        )

        recintos.forEach { r ->
            db.execSQL(
                "INSERT INTO recinto (nombre, descripcion, capacidadPersonas, tarifaBase, activo) VALUES (?, ?, ?, ?, 1)",
                arrayOf<Any>(r.nombre, r.descripcion, r.capacidadPersonas, r.tarifaBase)
            )
        }
    }

    private fun insertarUsuariosDemo(db: SupportSQLiteDatabase) {
        val ahora = System.currentTimeMillis()
        val usuarios = listOf(
            UsuarioDemo("Vecina", "Demo",
                        "vecino@zaror.test", "VECINO"),
            UsuarioDemo("Secretaria", "Demo",
                        "secretaria@zaror.test", "SECRETARIA"),
            UsuarioDemo("Tesorero", "Demo",
                        "tesoreria@zaror.test", "TESORERIA"),
            UsuarioDemo("Directiva", "Demo",
                        "directiva@zaror.test", "DIRECTIVA")
        )

        usuarios.forEach { u ->
            db.execSQL(
                "INSERT INTO usuario (nombre, apellido, email, telefono, claveHash, rolId, activo, fechaCreacion) " +
                        "SELECT ?, ?, ?, NULL, ?, r.id, 1, ? FROM rol r WHERE r.nombre = ?",
                arrayOf<Any>(u.nombre, u.apellido, u.email, CLAVE_DEMO_HASH, ahora, u.rol)
            )
        }
    }
}