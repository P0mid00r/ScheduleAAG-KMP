package com.pomidorka.scheduleaag.ad

import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.pomidorka.scheduleaag.utils.AppContext
import com.yandex.mobile.ads.appopenad.AppOpenAd
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.*

actual object AdManager {
    private object Ids {
        const val DEMO_AD = "demo-banner-yandex"
        const val APP_OPEN_AD = "R-M-8506845-3"
        const val BANNER_MAIN_ACTIVITY = "R-M-8506845-1"
        const val BANNER_SECOND_ACTIVITY = "R-M-8506845-2"
        const val BANNER_PDF_VIEWER_ACTIVITY = "R-M-8506845-4"
    }

    private var mAppOpenAd: AppOpenAd? = null
    private val activity = AppContext.activity

    private fun init() {
        val appOpenAdLoader = AppOpenAdLoader(activity)
        val adRequestConfiguration = AdRequestConfiguration.Builder(Ids.APP_OPEN_AD).build()

        val appOpenAdLoadListener: AppOpenAdLoadListener = object : AppOpenAdLoadListener {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                appOpenAd.show(activity)
                mAppOpenAd = appOpenAd
            }

            override fun onAdFailedToLoad(error: AdRequestError) {
            }
        }

        val appOpenAdEventListener = object : AppOpenAdEventListener {
            override fun onAdShown() = showOpenAppAd()

            override fun onAdDismissed() = clearAppOpenAd()

            override fun onAdClicked() { }

            override fun onAdFailedToShow(adError: AdError) { }

            override fun onAdImpression(impressionData: ImpressionData?) { }
        }

        mAppOpenAd?.setAdEventListener(appOpenAdEventListener)
        appOpenAdLoader.setAdLoadListener(appOpenAdLoadListener)
        appOpenAdLoader.loadAd(adRequestConfiguration)
    }

    actual fun showOpenAppAd() {
        if (mAppOpenAd == null) {
            init()
        }
        mAppOpenAd?.show(activity)
    }

    private fun clearAppOpenAd() {
        mAppOpenAd?.setAdEventListener(null)
        mAppOpenAd = null
    }

    fun getDisplayWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    @Composable
    actual fun AdBannerMainScreen(modifier: Modifier, backgroundColor: Color) {
        AdBanner(
            modifier = modifier,
            color = backgroundColor,
            id = Ids.BANNER_MAIN_ACTIVITY,
            adBannerContext = {}
        )
    }

    @Composable
    actual fun AdBannerScheduleScreen(modifier: Modifier, backgroundColor: Color) {
        AdBanner(
            modifier = modifier,
            color = backgroundColor,
            id = Ids.BANNER_PDF_VIEWER_ACTIVITY,
            adBannerContext = {}
        )
    }

    @Composable
    actual fun AdBannerAnyScreen(modifier: Modifier, backgroundColor: Color) {
        AdBanner(
            modifier = modifier,
            color = backgroundColor,
            id = Ids.BANNER_SECOND_ACTIVITY,
            adBannerContext = {}
        )
    }

    @Composable
    private fun AdBanner(
        modifier: Modifier,
        color: Color,
        id: String,
        widthBanner: Int = getDisplayWidth(),
        adBannerContext: (BannerAdView) -> Unit
    ) {
        var adBanner by remember { mutableStateOf<BannerAdView?>(null) }

        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .background(color),
            factory = {
                val banner = BannerAdView(it).apply {
                    setAdUnitId(id)
                    setAdSize(BannerAdSize.inlineSize(context, widthBanner, 85))
                    val adRequest: AdRequest = AdRequest.Builder().build()
                    loadAd(adRequest)
                }
                adBannerContext.invoke(banner)
                adBanner = banner
                return@AndroidView banner
            }
        )

        DisposableEffect(Unit) {
            onDispose {
                adBanner?.destroy()
            }
        }
    }
}