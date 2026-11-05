package com.example.vecinoszaror.ui.screens.gestion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.viewmodel.RecintoFormularioViewModel
import com.example.vecinoszaror.viewmodel.RecintosViewModel
import com.example.vecinoszaror.viewmodel.UsuarioFormularioViewModel
import com.example.vecinoszaror.viewmodel.UsuariosViewModel

/** Cada pestaña de administración y el permiso que hay que tener para verla. */
private enum class Seccion(val titulo: String, val permiso: String) {
    USUARIOS("Usuarios", Permisos.USUARIO_GESTIONAR),
    ROLES("Roles", Permisos.ROL_PERMISO_GESTIONAR),
    RECINTOS("Recintos", Permisos.RECINTO_GESTIONAR)
}

/** Administración: usuarios, roles / permisos, y recintos. En pestañas. */
@Composable
fun GestionScreen(
    usuariosViewModel: UsuariosViewModel,
    usuarioFormularioViewModel: UsuarioFormularioViewModel,
    recintosViewModel: RecintosViewModel,
    recintoFormularioViewModel: RecintoFormularioViewModel,
    sesion: SesionUsuario
) {
    // Solo se muestran las pestañas que el rol tiene permitidas
    val secciones = Seccion.entries.filter { sesion.tienePermiso(it.permiso) }
    var indice by rememberSaveable { mutableIntStateOf(0) }
    if (secciones.isEmpty()) return

    // coerceIn evita un índice fuera de rango si cambian los permisos
    val actual = indice.coerceIn(0, secciones.lastIndex)

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Gestión de datos",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        PrimaryTabRow(selectedTabIndex = actual) {
            secciones.forEachIndexed { posicion, seccion ->
                Tab(
                    selected = posicion == actual,
                    onClick = { indice = posicion },
                    text = { Text(seccion.titulo, maxLines = 1) }
                )
            }
        }

        when (secciones[actual]) {
            Seccion.USUARIOS -> UsuariosContenido(usuariosViewModel, usuarioFormularioViewModel, sesion)
            Seccion.ROLES -> Text("Roles y permisos: en construcción", modifier = Modifier.padding(16.dp))
            Seccion.RECINTOS -> RecintosContenido(recintosViewModel, recintoFormularioViewModel, sesion)
        }
    }
}
