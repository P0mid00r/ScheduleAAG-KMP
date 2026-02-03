package com.pomidorka.scheduleaag.ui.components.alertdialogs

class InfoDialogController : DialogControllerBase<InfoDialogController> {
    constructor(title: String? = null, message: String): super(
        isVisible = false,
        title = title,
        message = message,
        onConfirm = { it.hideDialog() },
        onDismiss = null
    )

    public override fun showDialog() = super.showDialog()

    public override fun hideDialog() = super.hideDialog()
}