# Nutierra – OYL Frutos Secos

Aplicación Android (Kotlin + Jetpack Compose + Material 3) desarrollada para la asignatura
**DSY1105 Desarrollo de Aplicaciones Móviles** (DuocUC), basada en el caso **OYL Frutos Secos**:
catálogo de frutos secos, semillas y deshidratados con perfiles de **Cliente** y **Administrador**.

## Integrantes
- DAVID SCHATZ
- CLAUDIO BERTIN
- SOFIA CARVAJAL

## Arquitectura (MVVM)
```
com.example.nutierra
├── data/local → Room (SQLite): NutierraDatabase, entidades, DAO y datos iniciales
├── data/repository → ProductoRepository, UsuarioRepository
├── data/session → SesionManager (usuario que inició sesión)
├── model → CampoFormulario y reglas de validación (Validaciones)
├── ui/viewmodel → ProductoViewModel, ProductoFormViewModel, LoginViewModel,
│ RegisterViewModel, PerfilViewModel (StateFlow)
├── ui/screens → Pantallas Compose de cliente y administrador
├── ui/components → Componentes reutilizables (mensajes de error con ícono)
└── navigation → Navigation Compose con transiciones animadas entre pantallas
```
Flujo de datos: **Pantalla → ViewModel → Repository → DAO → Room → SQLite**.

Las pantallas no contienen lógica de negocio: observan el estado del ViewModel con
`collectAsStateWithLifecycle()` y le envían eventos. Cada ViewModel expone un `StateFlow`
de solo lectura (`uiState`) y modifica su estado privado (`_uiState`). Las validaciones están
centralizadas en `model/Validaciones` y se prueban con JUnit (`app/src/test`).

## Funcionalidades implementadas

**Cuentas y sesión**
- Inicio de sesión y registro con validación por campo (ícono + mensaje) desde el ViewModel.
- Usuarios guardados en SQLite (correo único, contraseña con hash SHA-256) y rol Cliente/Administrador.
- Ícono de cuenta funcional en todas las pantallas: perfil del cliente con los datos de la sesión
  y cuenta del administrador con accesos rápidos y cierre de sesión.

**Productos (SQLite)**
- Catálogo, detalle de producto, gestión de productos e inventario leyendo y guardando en SQLite.
- Formulario de crear y editar producto con `ProductoFormViewModel`: validación de nombre,
  precio, stock e imagen con mensajes por campo.
- La lista de productos es un `Flow`: la interfaz se actualiza sola al cambiar la base de datos.

**Recursos nativos**
- Cámara (CameraX): foto del producto al crearlo.
- Escáner QR (ML Kit): los códigos `NUTIERRA:<id>` abren el producto correspondiente.
- Mapa de tiendas (Google Maps) con permiso de ubicación.

**Animaciones**
- Splash animado: entrada del logo con rebote, textos escalonados, hoja en movimiento,
  mensajes de carga con transición y salida con desvanecimiento.
- Transiciones entre pantallas: deslizamiento al avanzar y volver, fundido entre pestañas.
- Aparición animada de los mensajes de error y éxito en los formularios.

## Cuentas de demostración (datos ficticios)
| Rol | Correo | Contraseña |
|---|---|---|
| Cliente | cliente@nutierra.cl | Semillas2025! |
| Administrador | admin@nutierra.cl | AdminMaster2025! |

## Cómo ejecutar
1. Abrir el proyecto en Android Studio y esperar la sincronización de Gradle.
2. (Opcional, para el mapa) agregar en `local.properties`: `MAPS_API_KEY=tu_api_key`.
   La clave no se sube al repositorio.
3. Ejecutar en un emulador o dispositivo con Android 8.0 (API 26) o superior.
4. Para generar el APK: **Build → Generate App Bundles or APKs → Generate APKs**.

## Pruebas
Pruebas unitarias de las validaciones en `app/src/test` (correo, contraseña, nombre, precio y stock).
Para ejecutarlas: clic derecho en la carpeta `test` → **Run Tests**.

