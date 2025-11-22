package com.pomidorka.scheduleaag.ui.components.alertdialogs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

abstract class DialogControllerBase<T : DialogControllerBase<T>> {
    var isVisible by mutableStateOf(false)
        protected set

    var title by mutableStateOf("")
        protected set

    var message by mutableStateOf("")
        protected set

    var onConfirm by mutableStateOf<((T) -> Unit)?>(null)
        protected set

    var onDismiss by mutableStateOf<((T) -> Unit)?>(null)
        protected set

    constructor(
        isVisible: Boolean,
        title: String,
        message: String,
        onConfirm: ((T) -> Unit)? = null,
        onDismiss: ((T) -> Unit)? = null
    ) {
        this.isVisible = isVisible
        this.title = title
        this.message = message
        this.onConfirm = onConfirm
        this.onDismiss = onDismiss
    }

    protected open fun showDialog() {
        isVisible = true
    }

    protected open fun hideDialog() {
        isVisible = false
    }
}