package com.pomidorka.scheduleaag.ad

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

actual object AdManager {
    actual fun showOpenAppAd() {
    }

    @Composable
    actual fun AdBannerMainScreen(modifier: Modifier, backgroundColor: Color) {
        DefaultAdBanner(modifier, backgroundColor)
    }

    @Composable
    actual fun AdBannerScheduleScreen(modifier: Modifier, backgroundColor: Color) {
        DefaultAdBanner(modifier, backgroundColor)
    }

    @Composable
    actual fun AdBannerAnyScreen(modifier: Modifier, backgroundColor: Color) {
        DefaultAdBanner(modifier, backgroundColor)
    }
}