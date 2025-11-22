package com.pomidorka.scheduleaag.ad

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

actual object AdManager {
    actual fun showOpenAppAd() {
//        showFullscreenAd()
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

//private object AdId {
//    const val FULLSCREEN_AD_ID = "R-A-17422117-1"
//}

//@OptIn(ExperimentalWasmJsInterop::class)
//private fun showFullscreenAd() {
//    js("""
//yaContextCb.push(() => {
//    if (Ya.Context.AdvManager.getPlatform() === 'desktop') {
//        Ya.Context.AdvManager.render({
//            blockId: '$FULLSCREEN_AD_ID',
//            type: 'fullscreen',
//            platform: 'desktop',
//        });
//    } else {
//        Ya.Context.AdvManager.render({
//            blockId: '$FULLSCREEN_AD_ID',
//            type: 'fullscreen',
//            platform: 'touch',
//        });
//    }
//});
//    """)
//}