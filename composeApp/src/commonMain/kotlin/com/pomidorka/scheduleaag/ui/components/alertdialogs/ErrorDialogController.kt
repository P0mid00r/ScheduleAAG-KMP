package com.pomidorka.scheduleaag.ui.components.alertdialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberErrorDialogController(
    onConfirm: ((ErrorDialogController) -> Unit)
): ErrorDialogController {
    return remember(onConfirm) { ErrorDialogController(onConfirm) }
}

class ErrorDialogController : DialogControllerBase<ErrorDialogController> {
    constructor(onConfirm: ((ErrorDialogController) -> Unit)): super(
        isVisible = false,
        title = "Ошибка",
        message = "",
        onConfirm = onConfirm,
        onDismiss = null
    )

    fun showDialog(message: String) {
        super.message = message
        showDialog()
    }

    public override fun hideDialog() = super.hideDialog()
}