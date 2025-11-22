package com.pomidorka.scheduleaag.ui.components.alertdialogs

import AppConfig
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.pomidorka.scheduleaag.Strings
import com.pomidorka.scheduleaag.ui.components.BackgroundCells
import com.pomidorka.scheduleaag.ui.components.TopAppBar

@Composable
fun InfoDialog(
    visible: Boolean,
    onBackClick: () -> Unit
) {
    if (visible) {
        val state = rememberRichTextState().apply {
            val html = Strings.HTML_INFO_DIALOG.replace(
                oldValue = "</body>",
                newValue = "<br><b>Версия: ${AppConfig.VERSION_NAME}</b></br></body>"
            ).trimIndent()
            setHtml(html)
        }

        Dialog(
            onDismissRequest = onBackClick,
            properties = DialogProperties(
                dismissOnBackPress = false,
            )
        ) {
            BackgroundCells(
                Modifier.clip(
                    shape = RoundedCornerShape(24.dp)
                )
            ) {
                Column {
                    TopAppBar(
                        title = "Информация",
                        onBackClick = onBackClick,
                    )

                    RichText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        state = state,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}