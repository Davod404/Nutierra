
package com.example.nutierra.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nutierra.ui.screens.*

@Composable
fun NutierraNavigation() {
    val navController = rememberNavController()

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

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // =========================
        // CLIENTE
        // =========================

        composable("home") {
            HomeScreen(
                onCatalogClick = {
                    navController.navigate("catalog")
                },
                onOrdersClick = {
                    // Pendiente: OrdersScreen
                },
                onProfileClick = {
                    navController.navigate("profile")
                },
                onCartClick = {
                    navController.navigate("cart")
                }
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
                    navController.navigate("catalog") {
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
                onHomeClick = {
                    navController.navigate("home") {
                        launchSingleTop = true
                    }
                },
                onProductClick = { id ->
                    navController.navigate("product/$id")
                },
                onOrdersClick = {
                    // Pendiente: OrdersScreen
                },
                onProfileClick = {
                    navController.navigate("profile")
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
        ) {
            ProductDetailScreen(
                onBack = {
                    navController.popBackStack()
                },
                onAddToCart = { _, _ ->
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
                    navController.navigate("checkout")
                }
            )
        }

        composable("checkout") {
            CheckoutScreen(
                onBack = {
                    navController.popBackStack()
                },
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
            OrderSuccessScreen(
                onBack = {
                    navController.popBackStack()
                },
                onViewOrder = {
                    // Pendiente: OrdersScreen
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
                onOrdersClick = {
                    // Pendiente: OrdersScreen
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

        // =========================
        // ADMINISTRADOR
        // =========================

        // 1. Inicio Administrador
        composable("admin/home") {
            AdminHomeScreen(
                onProductsClick = {
                    goToAdminProducts()
                },
                onInventoryClick = {
                    goToAdminInventory()
                },
                onOrdersClick = {
                    // Pendiente: pedidos administrativos
                }
            )
        }

        // 2. Gestión de Productos
        composable("admin/products") {
            AdminProductsScreen(
                onHomeClick = {
                    goToAdminHome()
                },
                onInventoryClick = {
                    goToAdminInventory()
                },
                onOrdersClick = {
                    // Pendiente: pedidos administrativos
                },
                onAddProductClick = {
                    navController.navigate("admin/products/create")
                },
                onEditProductClick = { productId ->
                    navController.navigate(
                        "admin/products/edit/$productId"
                    )
                }
            )
        }

        // 3. Crear Producto
        composable("admin/products/create") {
            AdminCreateProductScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSaveProduct = { newProduct ->
                    // Pendiente: guardar newProduct en Room/API.
                    navController.popBackStack()
                }
            )
        }

        // 4. Editar Producto
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

            AdminEditProductScreen(
                productId = productId,
                onBack = {
                    navController.popBackStack()
                },
                onInventoryClick = {
                    goToAdminInventory()
                },
                onSaveProduct = { editedProduct ->
                    // Pendiente: guardar editedProduct en Room/API.
                    navController.popBackStack()
                }
            )
        }

        // 5. Inventario
        composable("admin/inventory") {
            AdminInventoryScreen(
                onHomeClick = {
                    goToAdminHome()
                },
                onProductsClick = {
                    goToAdminProducts()
                },
                onOrdersClick = {
                    // Pendiente: pedidos administrativos
                }
            )
        }
    }
}
