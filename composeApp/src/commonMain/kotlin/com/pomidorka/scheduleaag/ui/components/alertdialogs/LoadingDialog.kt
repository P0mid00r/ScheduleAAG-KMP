package com.pomidorka.scheduleaag.ui.components.alertdialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pomidorka.scheduleaag.ui.Brown
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.ui.components.alertdialogs.LoadingDialogController
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun LoadingDialogPreview() {
    val loadingDialogController = LoadingDialogController("Тестовая загрузка").apply {
        showDialog()
    }

    Box(Modifier.fillMaxSize()) {
        LoadingDialog(
            loadingDialogController = loadingDialogController
        )
    }
}

@Composable
fun LoadingDialog(
    loadingDialogController: LoadingDialogController,
    modifier: Modifier = Modifier
) {
    if (loadingDialogController.isVisible) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .border(
                        width = 4.dp,
                        color = Brown,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(Green)
            ) {
                CircularProgressIndicator(
                    color = Brown,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    )
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    text = loadingDialogController.message,
                    maxLines = 1,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}