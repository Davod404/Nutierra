
package com.example.nutierra.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutierra.data.local.entity.ProductoEntity
import com.example.nutierra.ui.viewmodel.CarritoViewModel
import com.example.nutierra.ui.viewmodel.PedidoViewModel
import com.example.nutierra.ui.viewmodel.ProductoViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nutierra.ui.screens.*

private const val DURACION_TRANSICION = 350

private val rutasDeBarra = setOf(
    "home", "catalog", "orders", "profile",
    "admin/home", "admin/products", "admin/inventory", "admin/orders", "admin/profile"
)

private fun usaFundido(desde: String?, hacia: String?): Boolean =
    desde == "splash" || (desde in rutasDeBarra && hacia in rutasDeBarra)

@Composable
fun NutierraNavigation() {
    val navController = rememberNavController()

    val productoViewModel: ProductoViewModel = viewModel()
    val productos by productoViewModel.productos.collectAsStateWithLifecycle()

    val carritoViewModel: CarritoViewModel = viewModel()
    val carrito by carritoViewModel.items.collectAsStateWithLifecycle()
    val errorCarrito by carritoViewModel.error.collectAsStateWithLifecycle()
    val procesandoCompra by carritoViewModel.procesando.collectAsStateWithLifecycle()
    val ultimoPedido by carritoViewModel.ultimoPedido.collectAsStateWithLifecycle()

    val pedidoViewModel: PedidoViewModel = viewModel()
    val misPedidos by pedidoViewModel.misPedidos.collectAsStateWithLifecycle()
    val todosLosPedidos by pedidoViewModel.todosLosPedidos.collectAsStateWithLifecycle()

    fun goToCatalog() {
        navController.navigate("catalog") {
            launchSingleTop = true
        }
    }

    fun goToAdminHome() {
        navController.navigate("admin/home") {
            popUpTo("admin/home") {
                inclusive = false
            }
            launchSingleTop = true
        }
    }

    fun goToAdminProducts() {
        navController.navigate("admin/products") {
            launchSingleTop = true
        }
    }

    fun goToAdminInventory() {
        navController.navigate("admin/inventory") {
            launchSingleTop = true
        }
    }

    fun goToProfile() {
        navController.navigate("profile") {
            launchSingleTop = true
        }
    }

    fun goToAdminProfile() {
        navController.navigate("admin/profile") {
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = "splash",

        enterTransition = {
            if (usaFundido(initialState.destination.route, targetState.destination.route)) {
                fadeIn(tween(DURACION_TRANSICION))
            } else {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(DURACION_TRANSICION)
                ) + fadeIn(tween(DURACION_TRANSICION))
            }
        },
        exitTransition = {
            if (usaFundido(initialState.destination.route, targetState.destination.route)) {
                fadeOut(tween(DURACION_TRANSICION))
            } else {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(DURACION_TRANSICION),
                    targetOffset = { it / 4 }
                ) + fadeOut(tween(DURACION_TRANSICION))
            }
        },
        popEnterTransition = {
            if (usaFundido(initialState.destination.route, targetState.destination.route)) {
                fadeIn(tween(DURACION_TRANSICION))
            } else {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    tween(DURACION_TRANSICION),
                    initialOffset = { it / 4 }
                ) + fadeIn(tween(DURACION_TRANSICION))
            }
        },
        popExitTransition = {
            if (usaFundido(initialState.destination.route, targetState.destination.route)) {
                fadeOut(tween(DURACION_TRANSICION))
            } else {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    tween(DURACION_TRANSICION)
                ) + fadeOut(tween(DURACION_TRANSICION))
            }
        }
    ) {

        composable("splash") {
            SplashScreen(
                onFinished = {
                    navController.navigate("login") {
                        popUpTo("splash") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // =========================
        // CLIENTE
        // =========================

        composable("home") {
            HomeScreen(
                productos = productos,
                onCatalogClick = {
                    navController.navigate("catalog")
                },
                onOrdersClick = {
                    navController.navigate("orders") {
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    navController.navigate("profile")
                },
                onCartClick = {
                    navController.navigate("cart")
                },
                onMapClick = {
                    navController.navigate("store_map")
                },
                onScanClick = {
                    navController.navigate("qr_scanner")
                },
            )
        }

        composable("login") {
            LoginScreen(
                onBack = {
                    navController.popBackStack()
                },
                onRegisterClick = {
                    navController.navigate("register")
                },
                onClientLogin = {
                    navController.navigate("home") {
                        popUpTo("login") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onAdminLogin = {
                    navController.navigate("admin/home") {
                        popUpTo("login") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onBack = {
                    navController.popBackStack()
                },
                onLoginClick = {
                    navController.navigate("login")
                },
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("catalog") {
            CatalogScreen(
                productos = productos,
                onHomeClick = {
                    navController.navigate("home") {
                        launchSingleTop = true
                    }
                },
                onMapClick = {
                    navController.navigate("store_map") {
                        launchSingleTop = true
                    }
                },
                onScanClick = {
                    navController.navigate("qr_scanner") {
                        launchSingleTop = true
                    }
                },
                onOrdersClick = {
                    navController.navigate("orders") {
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    navController.navigate("profile") {
                        launchSingleTop = true
                    }
                },
                onProductClick = { id ->
                    navController.navigate("product/$id")
                }
            )
        }
        composable(
            route = "product/{productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0

            ProductDetailScreen(
                producto = productos.firstOrNull { it.id == productId },
                onBack = {
                    navController.popBackStack()
                },
                onAddToCart = { productoId, cantidad ->
                    carritoViewModel.agregar(productoId, cantidad)
                    navController.navigate("cart")
                }
            )
        }

        composable("cart") {
            CartScreen(
                onBack = {
                    navController.popBackStack()
                },
                onContinueShopping = {
                    goToCatalog()
                },
                onCheckout = {
                    carritoViewModel.limpiarError()
                    navController.navigate("checkout")
                },
                onProfileClick = {
                    goToProfile()
                },
                /*
                items = carrito,
                onCambiarCantidad = { productoId, cantidad ->
                    carritoViewModel.cambiarCantidad(productoId, cantidad)
                },
                onEliminar = { productoId ->
                    carritoViewModel.eliminar(productoId)
                },
                mensajeError = errorCarrito
                 */
            )
        }

        composable("checkout") {
            CheckoutScreen(
                onBack = {
                    navController.popBackStack()
                },
                items = carrito,
                procesando = procesandoCompra,
                mensajeError = errorCarrito,
                onOrderSuccess = {
                    // Crea el pedido, descuenta stock y vacía el carrito (una transacción)
                    carritoViewModel.confirmarCompra {
                        navController.navigate("success") {
                            popUpTo("checkout") {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }

        composable("success") {
            OrderSuccessScreen(
                orderCode = ultimoPedido?.codigo ?: "",
                total = ultimoPedido?.total ?: 0,
                productCount = ultimoPedido?.cantidadProductos ?: 0,
                unitCount = ultimoPedido?.cantidadUnidades ?: 0,
                onBack = {
                    navController.popBackStack()
                },
                onViewOrder = {
                    navController.navigate("orders") {
                        launchSingleTop = true
                    }
                },
                onCatalogClick = {
                    navController.navigate("catalog") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onHomeClick = {
                    navController.navigate("home") {
                        launchSingleTop = true
                    }
                },
                onCatalogClick = {
                    goToCatalog()
                },
                onMapClick = {
                    navController.navigate("store_map") {
                        launchSingleTop = true
                    }
                },
                onScanClick = {
                    navController.navigate("qr_scanner") {
                        launchSingleTop = true
                    }
                },
                onOrdersClick = {
                    navController.navigate("orders") {
                        launchSingleTop = true
                    }
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("orders") {
            OrdersScreen(
                pedidos = misPedidos,
                onHomeClick = {
                    navController.navigate("home") {
                        launchSingleTop = true
                    }
                },
                onCatalogClick = {
                    navController.navigate("catalog") {
                        launchSingleTop = true
                    }
                },
                onMapClick = {
                    navController.navigate("store_map") {
                        launchSingleTop = true
                    }
                },
                onScanClick = {
                    navController.navigate("qr_scanner") {
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    navController.navigate("profile") {
                        launchSingleTop = true
                    }
                }
            )
        }

        // =========================
        // ADMINISTRADOR
        // =========================
        composable("admin/home") {
            AdminHomeScreen(
                onProductsClick = {
                    goToAdminProducts()
                },
                onInventoryClick = {
                    goToAdminInventory()
                },
                onOrdersClick = {
                    navController.navigate("admin/orders") {
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    goToAdminProfile()
                }
            )
        }

        composable("admin/products") {
            AdminProductsScreen(
                productos = productos,
                onHomeClick = {
                    goToAdminHome()
                },
                onInventoryClick = {
                    goToAdminInventory()
                },
                onOrdersClick = {
                    navController.navigate("admin/orders") {
                        launchSingleTop = true
                    }
                },
                onAddProductClick = {
                    navController.navigate("admin/products/create")
                },
                onEditProductClick = { productId ->
                    navController.navigate(
                        "admin/products/edit/$productId"
                    )
                },
                onProfileClick = {
                    goToAdminProfile()
                }
            )
        }

        composable("admin/products/create") { backStackEntry ->
            // URI de la foto que devuelve ProductCameraScreen
            val fotoTomada by backStackEntry.savedStateHandle
                .getStateFlow<String?>("product_image_uri", null)
                .collectAsStateWithLifecycle()

            AdminCreateProductScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSaveProduct = { newProduct ->
                    productoViewModel.insertarProducto(
                        ProductoEntity(
                            nombre = newProduct.name,
                            categoria = newProduct.category,
                            precio = newProduct.price,
                            stock = newProduct.stock,
                            peso = newProduct.weight,
                            imagenUri = newProduct.imageUri
                        )
                    )
                    navController.popBackStack()
                },
                onTakePhotoClick = {
                    navController.navigate("admin/product-camera")
                },
                fotoTomada = fotoTomada
            )
        }

        composable(
            route = "admin/products/edit/{productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val productId = backStackEntry.arguments
                ?.getInt("productId") ?: 1

            val producto = productos.firstOrNull { it.id == productId }

            AdminEditProductScreen(
                productId = productId,
                producto = producto,
                onBack = {
                    navController.popBackStack()
                },
                onInventoryClick = {
                    goToAdminInventory()
                },
                onSaveProduct = { editedProduct ->
                    // Se conservan los campos que el formulario no edita (stock, peso, etc.)
                    producto?.let {
                        productoViewModel.actualizarProducto(
                            it.copy(
                                nombre = editedProduct.name,
                                categoria = editedProduct.category,
                                precio = editedProduct.price,
                                imagenUri = editedProduct.imageUri ?: it.imagenUri
                            )
                        )
                    }
                    navController.popBackStack()
                }
            )
        }

        // 5. Inventario
        composable("admin/inventory") {
            AdminInventoryScreen(
                productos = productos,
                onActualizarStock = { id, nuevoStock ->
                    productoViewModel.actualizarStock(id, nuevoStock)
                },
                onHomeClick = {
                    goToAdminHome()
                },
                onProductsClick = {
                    goToAdminProducts()
                },
                onOrdersClick = {
                    navController.navigate("admin/orders") {
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    goToAdminProfile()
                }
            )
        }

        composable("admin/orders") {
            AdminOrdersScreen(
                pedidos = todosLosPedidos,
                onAvanzarEstado = { pedidoId, estadoActual ->
                    pedidoViewModel.avanzarEstado(pedidoId, estadoActual)
                },
                onHomeClick = {
                    navController.navigate("admin/home") {
                        launchSingleTop = true
                    }
                },
                onProductsClick = {
                    navController.navigate("admin/products") {
                        launchSingleTop = true
                    }
                },
                onInventoryClick = {
                    navController.navigate("admin/inventory") {
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    goToAdminProfile()
                }
            )
        }

        composable("admin/profile") {
            AdminProfileScreen(
                totalProductos = productos.size,
                onBack = {
                    navController.popBackStack()
                },
                onProductsClick = {
                    goToAdminProducts()
                },
                onInventoryClick = {
                    goToAdminInventory()
                },
                onOrdersClick = {
                    navController.navigate("admin/orders") {
                        launchSingleTop = true
                    }
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("store_map") {
            StoreMapScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("qr_scanner") {
            QRScannerScreen(
                onBack = {
                    navController.popBackStack()
                },
                onProductClick = { productId ->
                    navController.navigate("product/$productId")
                }
            )
        }
        composable("admin/product-camera") {
            ProductCameraScreen(
                onBack = {
                    navController.popBackStack()
                },
                onPhotoSelected = { imageUri ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("product_image_uri", imageUri)

                    navController.popBackStack()
                }
            )
        }

    }
}
