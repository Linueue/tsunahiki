package com.kldevs.tsunahiki

import androidx.compose.ui.window.ComposeUIViewController
import com.kldevs.tsunahiki.audio.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) { App() }