# VecinosZaror

Aplicación Android para gestionar los arriendos de las dependencias comunitarias de la **Junta de Vecinos Población Zaror** (Los Jazmines 425, Cerrillos) y dar transparencia a sus ingresos y gastos.
Proyecto académico de la asignatura **DSY1105 Desarrollo de Aplicaciones Móviles**. Todos los datos, montos y usuarios son **ficticios**.

### Equipo: Isidora Gómez Jeldres

---

## Qué puede hacer cada perfil

| Perfil | Qué hace en la app |
| --- | --- |
| **Vecino/a** | Solicitar el arriendo de un recinto, tambien puede cancelar sus reservas, y consultar los informes financieros publicados |
| **Secretaría** | Verificar la disponibilidad, confirma, rechaza o cancela reservas, y envía avisos |
| **Tesorería** | Registrar pagos y gastos (con comprobante opcional), ve el detalle financiero y genera informes |
| **Directiva** | Dar seguimiento a reservas y finanzas, publica los informes, envía avisos y administrar usuarios, recintos y roles junto a sus permisos |

### Funcionalidades

- **Reservas:** solicitud con recinto, fecha, hora, número de asistentes y motivo. El sistema impide dos reservas en el mismo recinto y horario. Incluye vista de disponibilidad por recinto y detalle de cada reserva.
- **Finanzas:** ingresos (pagos de arriendo), gastos por categoría (luz, agua, gas, internet, aseo, mantención, otro), saldo y filtros por recinto y categoría.
- **Comprobantes:** se adjuntan con la cámara del teléfono o desde la galería. El comprobante es opcional.
- **Informes:** tesorería genera el informe de un período y la directiva lo publica para que los vecinos lo vean, sino queda como borrador.
- **Avisos:** bandeja con avisos leídos y no leídos. Las reservas confirmadas, rechazadas o canceladas y los pagos generan avisos automáticos, y secretaría y directiva pueden enviar avisos a todos, solo a los vecinos o solo a la directiva.
- **Administración:** creación, edición y desactivación de usuarios, asignación de permisos a cada rol y gestión de recintos y tarifas.
- **Cuenta:** inicio de sesión, "olvidé mi clave" (se avisa a quienes administran usuarios), perfil, edición de datos personales y cambio de clave.
- **Diseño adaptable:** el login y la navegación cambian según el tamaño de pantalla (compacto, mediano y expandido).

### Permisos por rol

Los permisos se guardan en la base de datos y se asignan a cada rol, por lo que se pueden cambiar desde la app. Pero los permisos iniciales por rol son:

| Permiso | Vecino | Secretaría | Tesorería | Directiva |
| --- | :---: | :---: | :---: | :---: |
| Solicitar reserva | ✓ | ✓ | | |
| Ver y cancelar reservas propias | ✓ | ✓ | | |
| Ver todas las reservas | | ✓ | ✓ | ✓ |
| Verificar disponibilidad | | ✓ | | ✓ |
| Gestionar reservas (confirmar, rechazar, cancelar) | | ✓ | | ✓ |
| Registrar pagos y gastos | | | ✓ | |
| Ver detalle financiero | | | ✓ | ✓ |
| Generar informes | | | ✓ | |
| Publicar informes | | | | ✓ |
| Ver informes publicados | ✓ | ✓ | ✓ | ✓ |
| Enviar avisos | | ✓ | | ✓ |
| Gestionar usuarios, roles y recintos | | | | ✓ |

## Tecnologías

| Área | Herramienta |
| --- | --- |
| Lenguaje | Kotlin 2.2 |
| Interfaz | Jetpack Compose y Material Design 3 |
| Arquitectura | MVVM con flujo de datos unidireccional |
| Persistencia | Room (SQLite), con KSP |
| Asincronía | Corrutinas y Flow |
| Navegación | Navigation Compose |
| Imágenes | Coil |
| Android | `minSdk` 24 · `targetSdk` 37 |

## Arquitectura

Cada capa hace una sola cosa, y los datos pasan de una a otra en este orden: pantalla, ViewModel, repositorio, DAO y base de datos.

```
app/src/main/java/com/example/vecinoszaror/
├── model/            Tablas de Room, enums, permisos y sesión
│   └── uistate/      Estado de cada pantalla y sus errores de validación
├── database/         AppDatabase, DatabaseInitial (datos de prueba)
│   └── dao/          Consultas SQL (un DAO por tabla)
├── repository/       Reglas sobre los datos (hash de claves, entre otras)
├── viewmodel/        Estado de pantalla, validaciones y acciones
├── ui/
│   ├── screens/      Pantallas: login, reservas, finanzas, informes, avisos, gestión, perfil
│   ├── components/   Piezas reutilizables de Compose
│   └── utils/        Formatos y tamaños de pantalla
├── navigation/       Rutas y cambio entre pantallas
├── style/            Colores, tema y tipografía
└── util/             Fechas y archivos de comprobantes
```

Decisiones de diseño:

- Las validaciones viven en el ViewModel y se muestran todas a la vez.
- Las claves nunca se guardan en texto: se almacena su hash SHA-256 y se compara contra la base de datos.
- El formulario se limpia al entrar a una pantalla y no al salir, para evitar destellos.
- Los eventos de una sola vez (por ejemplo "ya se guardó, vuelve") viajan por `Channel` y no por el estado.
- Las referencias que importan para el historial usan `RESTRICT` al borrar, para no perder reservas ni pagos por accidente.

## Modelo de datos

La base tiene 10 tablas y 12 relaciones.

![Modelo relacional de VecinosZaror](docs/db_vecinos_zaror.svg)

| Tabla | Contenido |
| --- | --- |
| `rol`, `permiso`, `rol_permiso` | Perfiles, acciones permitidas y qué permisos tiene cada rol |
| `usuario` | Personas con acceso a la app; la clave se guarda como hash |
| `recinto` | Dependencias arrendables, con capacidad y tarifa base |
| `reserva` | Solicitudes de arriendo y su estado (pendiente, confirmada, rechazada, cancelada o finalizada) |
| `pago` | Pagos de cada reserva, con método y comprobante opcional |
| `gasto` | Gastos de la junta por categoría |
| `informe_financiero` | Informes de un período, con ingresos, gastos y saldo del momento en que se generaron |
| `notificacion` | Avisos para cada usuario |

### Usuarios de prueba

Todos usan la clave `demo1234`. Son datos ficticios.

| Perfil | Correo |
| --- | --- |
| Vecino/a | `vecino@zaror.test` |
| Secretaría | `secretaria@zaror.test` |
| Tesorería | `tesoreria@zaror.test` |
| Directiva | `directiva@zaror.test` |

Recintos de ejemplo: Salón Multiuso (80 personas), Sala de Reuniones (20) y Patio Techado (50). Los montos son ficticios.


## Prototipo (MVP Inicial)
![Prototipo (MVP Inicial)](docs/MVP_VecinosZaror.svg)
