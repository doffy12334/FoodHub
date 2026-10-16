package com.example.foodhub_app.navigation

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodhub_app.feature.customer.menu.ui.MenuRoute
import com.example.foodhub_app.feature.shared.auth.ui.LoginRoute
import com.example.foodhub_app.feature.shared.auth.ui.RegisterRoute

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Screen = Screen.Login,
    deepLinkUri: Uri? = null,
    onDeepLinkConsumed: () -> Unit = {}
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,

        // Hiệu ứng mặc định khi mở màn hình MỚI
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 300)
            ) + fadeIn(animationSpec = tween(durationMillis = 300))
        },

        // Hiệu ứng mặc định khi màn hình CŨ bị đẩy ra sau
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut(animationSpec = tween(durationMillis = 300))
        },

        // Hiệu ứng khi bấm QUAY LẠI
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = tween(durationMillis = 300)
            ) + fadeIn(animationSpec = tween(durationMillis = 300))
        },

        // Hiệu ứng khi màn hình hiện tại đóng lại
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut(animationSpec = tween(durationMillis = 300))
        }
    ) {

        // Màn hình Đăng nhập
        composable<Screen.Login> {
            LoginRoute(
                onBackClick = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.Login) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onLoginClick = { role ->
                    if (role?.equals("ADMIN", ignoreCase = true) == true) {
                        navController.navigate(Screen.Admin) {
                            popUpTo(Screen.Login) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.Login) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register)
                },
                onGuestQrClick = {
                    navController.navigate(Screen.QrScan)
                },
                onForgotPasswordClick = {
                    navController.navigate(Screen.ForgotPassword)
                }
            )
        }

        // Màn hình Đăng ký
        composable<Screen.Register> {
            RegisterRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginClick = {
                    navController.popBackStack<Screen.Login>(
                        inclusive = false
                    )
                },
                onRegisterSuccess = { role ->
                    if (role?.equals("ADMIN", ignoreCase = true) == true) {
                        navController.navigate(Screen.Admin) {
                            popUpTo(Screen.Home) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.Login) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        // Màn hình Quên mật khẩu
        composable<Screen.ForgotPassword> {
            Toast.makeText(context, "Màn hình Quên mật khẩu", Toast.LENGTH_SHORT).show()
        }

        // Màn hình Quét QR Khách hàng
        composable<Screen.QrScan> {
            Toast.makeText(context, "Màn hình Quét mã QR", Toast.LENGTH_SHORT).show()
        }

        // Màn hình Trang chủ Khách hàng (Home / Menu)
        composable<Screen.Home> {
            MenuRoute(
                onFoodClick = { foodId ->
                    navController.navigate(Screen.FoodDetail(foodId))
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onCartClick = {
                    navController.navigate(Screen.Cart)
                }
            )
        }

        // Màn hình Giỏ hàng
        composable<Screen.Cart> {
            Toast.makeText(context, "Màn hình Giỏ hàng", Toast.LENGTH_SHORT).show()
        }

        // Màn hình Chi tiết Món ăn
        composable<Screen.FoodDetail> {
            Toast.makeText(context, "Màn hình Chi tiết món ăn", Toast.LENGTH_SHORT).show()
        }

        // Màn hình Admin (Admin Dashboard)
        composable<Screen.Admin> {
            Toast.makeText(context, "Màn hình Quản trị Admin", Toast.LENGTH_LONG).show()
        }
    }
}
