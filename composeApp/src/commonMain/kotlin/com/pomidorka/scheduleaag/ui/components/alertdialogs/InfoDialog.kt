package com.pomidorka.scheduleaag.ui.components.alertdialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import com.pomidorka.scheduleaag.ui.Green

@Composable
fun InfoDialog(infoDialogController: InfoDialogController) {
    if (infoDialogController.isVisible) {
        AlertDialog(
            title = infoDialogController.title?.let { { Text(it) } },
            text = { Text(infoDialogController.message) },
            onDismissRequest = { },
            confirmButton = {
                TextButton(
                    onClick = { infoDialogController.onConfirm?.invoke(infoDialogController) },
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