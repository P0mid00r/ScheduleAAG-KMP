package com.pomidorka.scheduleaag.ui.components.alertdialogs

class LoadingDialogController : DialogControllerBase<LoadingDialogController> {
    constructor(message: String): super(
        isVisible = false,
        title = "",
        message = message,
        onConfirm = null,
        onDismiss = null
    )

    public override fun showDialog() = super.showDialog()

    public override fun hideDialog() = super.hideDialog()
}