package com.kldevs.tsunahiki.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kldevs.tsunahiki.game.LanguageType
import com.kldevs.tsunahiki.game.MainGame
import com.kldevs.tsunahiki.menu.LanguageMenu
import com.kldevs.tsunahiki.menu.LanguageSettings
import com.kldevs.tsunahiki.menu.ProfileMenu
import com.kldevs.tsunahiki.menu.ShopMenu
import com.kldevs.tsunahiki.menu.StartMenu
import com.kldevs.tsunahiki.purchases.GetCoinsMenu

typealias NavFn = () -> Unit
typealias NavToFn = (Any) -> Unit

inline fun <reified T: Any> NavController.navTo(route: T) {
    this.navigate(route)
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = StartMenuRoute,
    ) {
        composable<StartMenuRoute> {
            StartMenu(
                onStart = { navController.navigate(LanguageMenuRoute) },
                onShop = { navController.navigate(ShopMenuRoute) },
                onProfile = { navController.navigate(ProfileMenuRoute) },
            )
        }
        composable<LanguageMenuRoute> {
            LanguageMenu(
                onBack = { navController.popBackStack() },
                navTo = { navController.navigate(it) },
            )
        }
        composable<ProfileMenuRoute> {
            ProfileMenu(
                onBack = { navController.popBackStack() },
            )
        }
        composable<ShopMenuRoute> {
            ShopMenu(
                onBack = { navController.popBackStack() },
                onGetCoins = { navController.navigate(GetCoinsMenuRoute) },
            )
        }
        composable<LanguageSettingsMenuRoute> {
            val route = it.toRoute<LanguageSettingsMenuRoute>()

            LanguageSettings(
                language = route.language,
                onBack = { navController.popBackStack() },
                navTo = { navController.navigate(it) },
            )
        }
        composable<GameplayRoute> {
            val route = it.toRoute<GameplayRoute>()

            MainGame(
                isGuided = route.isGuided,
                onBack = { navController.popBackStack() },
                navTo = { navController.navigate(it) },
            )
        }
        composable<GetCoinsMenuRoute> {
            GetCoinsMenu(
                onBack = { navController.popBackStack() },
            )
        }
    }
}