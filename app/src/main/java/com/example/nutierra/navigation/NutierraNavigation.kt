package com.example.nutierra.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.nutierra.ui.screens.HomeScreen
import com.example.nutierra.ui.screens.LoginScreen
import com.example.nutierra.ui.screens.RegisterScreen
import com.example.nutierra.ui.screens.ProfileScreen
import com.example.nutierra.ui.screens.CatalogScreen
import com.example.nutierra.ui.screens.ProductDetailScreen
import com.example.nutierra.ui.screens.CartScreen
import com.example.nutierra.ui.screens.CheckoutScreen
import com.example.nutierra.ui.screens.OrderSuccessScreen

@Composable
fun NutierraNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(
                onCatalogClick = {
                    navController.navigate("catalog")
                },
                onOrdersClick = {
                    // Pendiente: crear OrdersScreen
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
                    navController.navigate("catalog")
                },
                onAdminLogin = {
                    navController.navigate("catalog")
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
                    navController.navigate("login")
                }
            )
        }

        composable("catalog") {
            CatalogScreen(
                onHomeClick = {
                    navController.navigate("home")
                },
                onProductClick = { productId ->
                    navController.navigate("product/$productId")
                },
                onOrdersClick = {
                    navController.navigate("cart")
                },
                onProfileClick = {
                    navController.navigate("profile")
                }
            )
        }

        composable("product/{productId}") {
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
                    navController.navigate("catalog")
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
                    navController.navigate("success")
                }
            )
        }

        composable("success") {
            OrderSuccessScreen(
                onBack = {
                    navController.popBackStack()
                },
                onViewOrder = {
                    // Se conectará a Mis Pedidos
                },
                onCatalogClick = {
                    navController.navigate("catalog") {
                        popUpTo("catalog") {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onHomeClick = {
                    navController.navigate("home")
                },
                onCatalogClick = {
                    navController.navigate("catalog")
                },
                onOrdersClick = {
                    navController.navigate("cart")
                },
                onLogout = {
                    navController.navigate("login")
                }
            )
        }
    }
}
