package com.example.nutierra.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutierra.data.local.entity.ProductoEntity
import com.example.nutierra.data.session.SesionManager
import com.example.nutierra.ui.viewmodel.ProductoViewModel
import com.example.nutierra.ui.viewmodel.CarritoViewModel
import com.example.nutierra.ui.viewmodel.PedidoViewModel
import com.example.nutierra.ui.screens.*
import com.example.nutierra.ui.viewmodel.ProductoFormViewModel

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
    val ultimoPedido by carritoViewModel.ultimoPedido.collectAsStateWithLifecycle()
    val pedidoViewModel: PedidoViewModel = viewModel()
    val misPedidos by pedidoViewModel.misPedidos.collectAsStateWithLifecycle()
    val todosLosPedidos by pedidoViewModel.todosLosPedidos.collectAsStateWithLifecycle()

    fun goToHome() {
        navController.navigate("home") {
            launchSingleTop = true
        }
    }

    fun goToCatalog() {
        navController.navigate("catalog") {
            launchSingleTop = true
        }
    }

    fun goToOrders() {
        navController.navigate("orders") {
            launchSingleTop = true
        }
    }

    fun goToProfile() {
        navController.navigate("profile") {
            launchSingleTop = true
        }
    }

    fun goToStoreMap() {
        navController.navigate("store_map") {
            launchSingleTop = true
        }
    }

    fun goToQrScanner() {
        navController.navigate("qr_scanner") {
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

    fun goToAdminOrders() {
        navController.navigate("admin/orders") {
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
                    val usuario = SesionManager.usuarioActual.value
                    val destino = when {
                        usuario == null -> "login"
                        usuario.rol == "ADMIN" -> "admin/home"
                        else -> "home"
                    }
                    navController.navigate(destino) {
                        popUpTo("splash") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                productos = productos,
                onCatalogClick = { goToCatalog() },
                onOrdersClick = { goToOrders() },
                onProfileClick = { goToProfile() },
                onCartClick = { navController.navigate("cart") },
                onMapClick = { goToStoreMap() },
                onScanClick = { goToQrScanner() },
                viewModel = carritoViewModel
            )
        }

        composable("login") {
            LoginScreen(
                onRegisterClick = { navController.navigate("register") },
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
                onBack = { navController.popBackStack() },
                onLoginClick = { navController.navigate("login") },
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
                onHomeClick = { goToHome() },
                onMapClick = { goToStoreMap() },
                onScanClick = { goToQrScanner() },
                onOrdersClick = { goToOrders() },
                onProfileClick = { goToProfile() },
                onCartClick = { navController.navigate("cart") },
                onProductClick = { id -> navController.navigate("product/$id") },
                viewModel = carritoViewModel
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
                producto = productos.firstOrNull {
                    it.id == productId
                },
                onBack = { navController.popBackStack() },
                onAddToCart = { productoId, cantidad ->
                    carritoViewModel.agregar(productoId, cantidad)
                    navController.navigate("cart")
                }
            )
        }

        composable("cart") {
            CartScreen(
                viewModel = carritoViewModel,
                onBack = { navController.popBackStack() },
                onContinueShopping = { goToCatalog() },
                onCheckout = { navController.navigate("checkout") },
                onProfileClick = { goToProfile() }
            )
        }

        composable("checkout") {
            CheckoutScreen(
                viewModel = carritoViewModel,
                onBack = { navController.popBackStack() },
                onOrderSuccess = {
                    navController.navigate("success") {
                        popUpTo("checkout") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("success") {
            val ultimoPedidoItems by carritoViewModel.ultimoPedidoItems.collectAsStateWithLifecycle()

            OrderSuccessScreen(
                orderCode = ultimoPedido?.codigo ?: "",
                total = ultimoPedido?.total ?: 0,
                productCount = ultimoPedido?.cantidadProductos ?: 0,
                unitCount = ultimoPedido?.cantidadUnidades ?: 0,
                items = ultimoPedidoItems,
                onBack = { navController.popBackStack() },
                onViewOrder = { goToOrders() },
                onCatalogClick = { goToCatalog() }
            )
        }

        composable("profile") {
            ProfileScreen(
                onHomeClick = { goToHome() },
                onCatalogClick = { goToCatalog() },
                onMapClick = { goToStoreMap() },
                onScanClick = { goToQrScanner() },
                onOrdersClick = { goToOrders() },
                onEditProfileClick = {
                    navController.navigate("profile/edit")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("profile/edit") {
            EditProfileScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable("orders") {
            OrdersScreen(
                pedidos = misPedidos,
                onHomeClick = { goToHome() },
                onCatalogClick = { goToCatalog() },
                onMapClick = { goToStoreMap() },
                onScanClick = { goToQrScanner() },
                onProfileClick = { goToProfile() }
            )
        }

        composable("admin/home") {
            AdminHomeScreen(
                onProductsClick = { goToAdminProducts() },
                onInventoryClick = { goToAdminInventory() },
                onOrdersClick = { goToAdminOrders() },
                onProfileClick = { goToAdminProfile() }
            )
        }

        composable("admin/products") {
            AdminProductsScreen(
                productos = productos,
                onHomeClick = { goToAdminHome() },
                onInventoryClick = { goToAdminInventory() },
                onOrdersClick = { goToAdminOrders() },
                onAddProductClick = { navController.navigate("admin/products/create") },
                onEditProductClick = { productId ->
                    navController.navigate("admin/products/edit/$productId")
                },
                onDeleteProductClick = { productId ->
                    productos.firstOrNull {
                        it.id == productId
                    }?.let { producto ->
                        productoViewModel.eliminarProducto(producto)
                    }
                },
                onProfileClick = { goToAdminProfile() }
            )
        }

        composable("admin/products/create") { backStackEntry ->
            val fotoTomada by backStackEntry.savedStateHandle
                .getStateFlow<String?>("product_image_uri", null)
                .collectAsStateWithLifecycle()

            AdminCreateProductScreen(
                onBack = { navController.popBackStack() },
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
                onTakePhotoClick = { navController.navigate("admin/product-camera") },
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
            val viewModel: ProductoFormViewModel = viewModel(
                viewModelStoreOwner = backStackEntry
            )
            val productId = backStackEntry.arguments
                ?.getInt("productId") ?: 1
            val producto = productos.firstOrNull { it.id == productId }

            AdminEditProductScreen(
                viewModel = viewModel,
                productId = productId,
                producto = producto,
                onBack = { navController.popBackStack() },
                onInventoryClick = { goToAdminInventory() },
                onSaveProduct = { editedProduct ->
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

        composable("admin/inventory") {
            AdminInventoryScreen(
                productos = productos,
                onActualizarStock = { id, nuevoStock ->
                    productoViewModel.actualizarStock(id, nuevoStock)
                },
                onHomeClick = { goToAdminHome() },
                onProductsClick = { goToAdminProducts() },
                onOrdersClick = { goToAdminOrders() },
                onProfileClick = { goToAdminProfile() }
            )
        }

        composable("admin/orders") {
            AdminOrdersScreen(
                pedidos = todosLosPedidos,
                onAvanzarEstado = { pedidoId, estadoActual ->
                    pedidoViewModel.avanzarEstado(pedidoId, estadoActual)
                },
                onHomeClick = { goToAdminHome() },
                onProductsClick = { goToAdminProducts() },
                onInventoryClick = { goToAdminInventory() },
                onProfileClick = { goToAdminProfile() }
            )
        }

        composable("admin/profile") {
            AdminProfileScreen(
                onBack = { navController.popBackStack() },
                onEditProfileClick = {
                    navController.navigate("profile/edit")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                totalProductos = productos.size,
                totalPedidos = todosLosPedidos.size
            )
        }

        composable("store_map") {
            StoreMapScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("qr_scanner") {
            QRScannerScreen(
                onBack = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate("product/$productId")
                },
                existeProducto = { productId ->
                    productos.any { it.id == productId }
                },
            )
        }
        composable("admin/product-camera") {
            ProductCameraScreen(
                onBack = { navController.popBackStack() },
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