# Nutierra – OYL Frutos Secos

Aplicación Android (Kotlin + Jetpack Compose + Material 3) desarrollada para la asignatura
**DSY1105 Desarrollo de Aplicaciones Móviles** (DuocUC), basada en el caso **OYL Frutos Secos**:
catálogo de frutos secos, semillas y deshidratados con perfiles de **Cliente** y **Administrador**.

## Integrantes
- _(completar)_
- _(completar)_

## Arquitectura (MVVM)
```
com.example.nutierra
├── data/local          → Room (SQLite): NutierraDatabase, entidades, DAO, datos iniciales
├── data/repository     → ProductoRepository, UsuarioRepository
├── model               → CampoFormulario y reglas de validación (Validaciones)
├── ui/viewmodel        → ProductoViewModel, LoginViewModel, RegisterViewModel (StateFlow)
├── ui/screens          → Pantallas Compose
├── ui/components       → Componentes reutilizables (mensajes de error con ícono)
└── navigation          → Navigation Compose (rutas de cliente y administrador)
```
Las pantallas no contienen lógica de negocio: observan el estado del ViewModel con
`collectAsStateWithLifecycle()` y le envían eventos. Las validaciones están centralizadas en
`model/Validaciones` y se prueban con JUnit (`app/src/test`).

## Funcionalidades implementadas
- Inicio de sesión y registro con validación por campo (ícono + mensaje) desde el ViewModel.
- Usuarios guardados en SQLite (contraseña con hash SHA-256) y rol Cliente/Administrador.
- Catálogo, detalle de producto, gestión de productos e inventario leyendo y guardando en SQLite.
- Recursos nativos: cámara (foto de producto), escáner QR (`NUTIERRA:<id>`) y mapa de tiendas.
- Animaciones de aparición en los mensajes de error y éxito de los formularios (login y registro).

## Cuentas de demostración (datos ficticios)
| Rol | Correo | Contraseña |
|---|---|---|
| Cliente | cliente@nutierra.cl | Semillas2025! |
| Administrador | admin@nutierra.cl | AdminMaster2025! |

## Cómo ejecutar
1. Abrir el proyecto en Android Studio y esperar la sincronización de Gradle.
2. (Opcional, para el mapa) agregar en `local.properties`: `MAPS_API_KEY=tu_api_key`.
3. Ejecutar en un emulador o dispositivo con Android 8.0 (API 26) o superior.
4. Para generar el APK: **Build → Generate App Bundles or APKs → Generate APKs**.

## Planificación
Trello: _(agregar enlace)_
