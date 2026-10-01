package com.kldevs.tsunahiki

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.kldevs.tsunahiki.game.MainGame
import com.kldevs.tsunahiki.menu.StartMenu
import com.kldevs.tsunahiki.navigation.AppNavHost
import com.kldevs.tsunahiki.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        AppNavHost()
    }
}