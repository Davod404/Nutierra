Nutierra – OYL Frutos Secos
Aplicación Android (Kotlin + Jetpack Compose + Material 3) desarrollada para la asignatura DSY1105 Desarrollo de Aplicaciones Móviles (DuocUC), basada en el caso OYL Frutos Secos: catálogo de frutos secos, semillas y deshidratados con perfiles de Cliente y Administrador.

Integrantes
DAVID SCHATZ

CLAUDIO BERTIN

SOFIA CARVAJAL

Arquitectura (MVVM)
App Android
com.example.nutierra

data/local → Room (SQLite): NutierraDatabase, entidades, DAO y datos iniciales

data/remote → Retrofit: ApiService, RetrofitClient, ApiRepository y DTOs

data/repository → ProductoRepository, UsuarioRepository, CarritoRepository, etc.

data/session → SesionManager y SessionPreferences (usuario y sesión persistida)

model → CampoFormulario, Validaciones y modelos de dominio (Tienda)

ui/viewmodel → ViewModels con StateFlow (productos, carrito, pedidos, perfil, etc.)

ui/screens → Pantallas Compose de cliente y administrador

ui/components → Componentes reutilizables (errores, campanita de notificaciones)

navigation → Navigation Compose con transiciones animadas entre pantallas

Flujo de datos: Pantalla → ViewModel → Repository → DAO → Room → SQLite para datos locales, y Pantalla → ViewModel → ApiRepository → Retrofit → Spring Boot para datos remotos.

Las pantallas no contienen lógica de negocio: observan el estado del ViewModel con collectAsStateWithLifecycle() y le envían eventos. Cada ViewModel expone un StateFlow de solo lectura (uiState) y modifica su estado privado (_uiState).

Backend Spring Boot
com.example.nutierra.nutierra_api

productos/controller y service → GET /api/productos

pedidos/controller y service → GET /api/pedidos

usuarios/controller y service → GET /api/usuarios, POST /api/usuarios/login

Tres módulos lógicos (productos, pedidos y usuarios) dentro de un mismo proyecto Spring Boot (modular monolito), con datos mock para el entorno de simulación.

Funcionalidades implementadas
Cuentas y sesión

Inicio de sesión y registro con validación por campo desde el ViewModel.

Usuarios guardados en SQLite (correo único, contraseña con hash SHA-256) y rol Cliente/Administrador.

Sesión persistida con DataStore: el usuario puede elegir "Recordar sesión".

Ícono de cuenta funcional en todas las pantallas: perfil del cliente con los datos de la sesión y cuenta del administrador con accesos rápidos y cierre de sesión.

Productos (SQLite)

Catálogo, detalle de producto, gestión de productos e inventario leyendo y guardando en SQLite.

Formulario de crear y editar producto con ProductoFormViewModel: validación de nombre, precio, stock e imagen con mensajes por campo.

La lista de productos es un Flow: la interfaz se actualiza sola al cambiar la base de datos.

Carrito y pedidos

Carrito por usuario con validación de stock, checkout, simulación de pago y creación de pedidos.

Historial de pedidos con estados (Pendiente, Confirmado, Preparado, Entregado).

Simulación de rechazo de negocio (por ejemplo, fondos insuficientes) sin perder el carrito.

Notificaciones

Notificaciones locales con canales, permiso POST_NOTIFICATIONS y deep link a la app.

Pantalla de notificaciones con badge de no leídas.

Simulador cada 30 segundos:

Cliente: avanza el estado de un pedido.

Administrador: crea un pedido aleatorio con productos y stock aleatorio.

Recursos nativos

Cámara (CameraX): foto del producto al crearlo.

Escáner QR (ML Kit): los códigos NUTIERRA:<id> abren el producto correspondiente.

Mapa de tiendas con osmdroid (OpenStreetMap), con permiso de ubicación y selector de tiendas.

Animaciones

Splash animado: entrada del logo, textos escalonados, mensajes de carga y salida con desvanecimiento.

Transiciones entre pantallas: deslizamiento al avanzar y volver, fundido entre pestañas.

Aparición animada de los mensajes de error y éxito en los formularios.

Modo oscuro

Tema claro/oscuro automático según el sistema (isSystemInDarkTheme).

Barra de navegación del sistema con enableEdgeToEdge y estilo acorde al tema.

Cuentas de demostración (datos ficticios)
Rol	Correo	Contraseña
Cliente	cliente@nutierra.cl	Semillas2025!
Administrador	admin@nutierra.cl	AdminMaster2025!
Cómo ejecutar
App Android
Abrir el proyecto en Android Studio y esperar la sincronización de Gradle.

Ejecutar en un emulador o dispositivo con Android 8.0 (API 26) o superior.

Para generar el APK: Build → Generate App Bundles or APKs → Generate APKs.

Backend Spring Boot
Abrir el proyecto nutierra-api en IntelliJ (o tu IDE).

Ejecutar ./mvnw spring-boot:run.

El backend estará disponible en http://localhost:8080.

Endpoints disponibles:

GET http://localhost:8080/api/productos

GET http://localhost:8080/api/pedidos

GET http://localhost:8080/api/usuarios

POST http://localhost:8080/api/usuarios/login (body: {"correo": "cliente@nutierra.cl", "contrasena": "Semillas2025!"})

Conexión App ↔ Backend
En RetrofitClient.kt, ajusta la BASE_URL:

Emulador: http://10.0.2.2:8080/

Celular físico: http://<IP_DE_TU_PC>:8080/ (ej. http://192.168.100.44:8080/)

Pruebas
Tests unitarios (PC, sin emulador)
Comando: ./gradlew testDebugUnitTest

ApiRepositoryTest (MockK + Coroutines Test): verifica que el repositorio devuelve los productos o propaga el error.

ApiProductoViewModelTest (MockK + Coroutines Test): verifica los estados de carga, datos y error del ViewModel.

Tests instrumentados (UI, con emulador o celular)
Comando: ./gradlew connectedDebugAndroidTest

ApiProductosScreenTest (Compose UI Test): verifica el indicador de carga, los datos y el error.

Validaciones unitarias
En app/src/test también hay pruebas de las validaciones (correo, contraseña, nombre, precio, stock).

Evidencia de testing
Tests unitarios en verde (5 tests).

Test de UI en verde (3 tests).

Capturas de Logcat con los 3 módulos de la API respondiendo (Productos: 8, Pedidos: 2, Usuarios: 2, Login: Cliente Demo).
