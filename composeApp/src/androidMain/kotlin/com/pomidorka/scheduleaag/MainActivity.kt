package com.pomidorka.scheduleaag

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Vibrator
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pomidorka.scheduleaag.utils.AndroidVibrator
import com.pomidorka.scheduleaag.utils.AppContext
import com.pomidorka.scheduleaag.utils.Log
import ru.ok.tracer.Tracer.setUserId

class MainActivity : ComponentActivity() {
    private val deviceId
        get() = Settings.System.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        Log.info("MainActivity.kt") {
            "Запуск Android приложения"
        }

        setUserId(deviceId)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        if (isNotLargeDisplay()) {
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        }

        AppContext.applicationContext = this.applicationContext
        AppContext.activity = this
        AndroidVibrator.vibrator = getSystemService(Vibrator::class.java) as Vibrator

        setContent {
            App()
        }
    }

    private fun isNotLargeDisplay() = !isLargeDisplay()

    private fun isLargeDisplay(): Boolean {
        val configuration = resources.configuration
        val uiMod = configuration.uiMode and Configuration.UI_MODE_TYPE_MASK
        val screenLayout = configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

        val isTablet = screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE
                || screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE

        return when (uiMod) {
            Configuration.UI_MODE_TYPE_TELEVISION -> true
            Configuration.UI_MODE_TYPE_NORMAL -> isTablet
            else -> false
        }
    }
}