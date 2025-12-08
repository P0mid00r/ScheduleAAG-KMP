package com.pomidorka.scheduleaag

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pomidorka.scheduleaag.ad.AdManager
import com.pomidorka.scheduleaag.data.SettingsRepository
import com.pomidorka.scheduleaag.ui.components.alertdialogs.LoadingDialog
import com.pomidorka.scheduleaag.ui.components.alertdialogs.LoadingDialogController
import com.pomidorka.scheduleaag.ui.components.alertdialogs.ProgressDialog
import com.pomidorka.scheduleaag.ui.components.alertdialogs.UpdaterDialog
import com.pomidorka.scheduleaag.ui.navigation.AppNavigation
import com.pomidorka.scheduleaag.updater.UpdateProgressListener
import com.pomidorka.scheduleaag.updater.Updates
import com.pomidorka.scheduleaag.updater.getUpdater
import com.pomidorka.scheduleaag.updater.update
import com.pomidorka.scheduleaag.utils.Log
import com.pomidorka.scheduleaag.utils.currentPlatform
import kotlinx.coroutines.launch

@Composable
fun App() {
    AdManager.showOpenAppAd()
    SettingsRepository.initialization()

    val scope = rememberCoroutineScope()
    val updater = getUpdater()
    val platform = currentPlatform()
    var progress by remember { mutableIntStateOf(-1) }
    val showProgressBar by remember {
        derivedStateOf { progress in 0..100 }
    }
    val updaterListener = if (!platform.type.isDesktop) {
        object : UpdateProgressListener {
            override fun onProgress(percentage: Int) {
                progress = percentage
            }

            override fun onCompleted() {
            }

            override fun onError(throwable: Throwable) {
                progress = -1
            }
        }
    } else null
    var updates by remember { mutableStateOf<Updates?>(null) }
    val loadingDialogController = LoadingDialogController(
        message = "Проверка наличия обновлений..."
    )

    LaunchedEffect(Unit) {
        if (platform.type.isNotWeb) {
            scope.launch {
                loadingDialogController.showDialog()
                Log.info("App.kt") { "Проверка наличия обновлений" }
                updates = updater.checkAvailableUpdates()
                loadingDialogController.hideDialog()

                if (updates == null) {
                    Log.info("App.kt") { "Обновлений не найдено" }
                } else {
                    Log.info("App.kt") { "Найдено обновление ${updates?.versionName}" }
                }
            }
        }
    }

    LoadingDialog(loadingDialogController)

    updates?.let {
        UpdaterDialog(
            versionName = it.versionName,
            whatsNew = it.whatsNew,
            onUpdateClick = {
                it.update(updaterListener)
            }
        )
    }

    if (showProgressBar) {
        ProgressDialog(progress)
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavigation()
        }
    }
}