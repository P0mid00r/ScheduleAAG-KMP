package com.pomidorka.scheduleaag.ui.components.alertdialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pomidorka.scheduleaag.ui.Brown
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.utils.getVibrator
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun UpdaterDialogPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        UpdaterDialog(
            versionName = "1.0.0",
            whatsNew = "- Переработан алгоритм получения расписания, теперь меньше шанс получить вылет или зависание приложения.\n" +
                    "- Отображение оповещения если сервер не отвечает.\n" +
                    "- Улучшение производительности.\n" +
                    "- Улучшение стабильности системы.",
            onUpdateClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdaterDialog(
    versionName: String,
    whatsNew: String,
    onUpdateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vibrator = getVibrator()

    BasicAlertDialog(
        onDismissRequest = {},
        content = {
            Box(
                modifier = modifier
                    .width(350.dp)
                    .background(
                        color = Green,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .border(
                        width = 5.dp,
                        color = Brown,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .clip(shape = RoundedCornerShape(25.dp)),
            ) {
                Column(
                    modifier = modifier.padding(24.dp),
                ) {
                    Text(
                        text = "Вышло новое обновление\n$versionName",
                        color = Color.White,
                        fontSize = 20.sp,
                        modifier = modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )

                    if (whatsNew.isNotEmpty()) {
                        Text(
                            text = whatsNew,
                            color = Color.White,
                            fontSize = 15.sp,
                            modifier = modifier.padding(
                                top = 12.dp,
                                bottom = 12.dp
                            ),
                        )
                    }

                    TextButton(
                        onClick = {
                            onUpdateClick()
                            vibrator.vibrateClick()
                        },
                        modifier = modifier.align(Alignment.End),
                    ) {
                        Text(
                            text = "СКАЧАТЬ ОБНОВЛЕНИЕ",
                            color = Brown,
                        )
                    }
                }
            }
        }
    )
}