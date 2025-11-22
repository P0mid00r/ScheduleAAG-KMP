package com.pomidorka.scheduleaag.ui.components.alertdialogs

import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.ui.window.DialogProperties
import com.pomidorka.scheduleaag.ui.Green

@Composable
fun ErrorDialog(errorDialogController: ErrorDialogController) {
    if (errorDialogController.isVisible) {
        AlertDialog(
            title = { Text(errorDialogController.title) },
            text = { Text(errorDialogController.message) },
            onDismissRequest = { },
            confirmButton = {
                TextButton(
                    onClick = { errorDialogController.onConfirm?.invoke(errorDialogController) },
                ) {
                    Text("Хорошо", color = Green)
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
        )
    }
}