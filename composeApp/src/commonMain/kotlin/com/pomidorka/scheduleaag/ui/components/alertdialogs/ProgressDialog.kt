package com.pomidorka.scheduleaag.ui.components.alertdialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pomidorka.scheduleaag.ui.Brown
import com.pomidorka.scheduleaag.ui.Green

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressDialog(progress: Int) {
    BasicAlertDialog(
        onDismissRequest = {}
    ) {
        Box(
            modifier = Modifier
                .requiredHeight(100.dp)
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
            Column {
                Text(
                    modifier = Modifier.padding(
                        top = 16.dp,
                        start = 16.dp
                    ),
                    text = "Загрузка файла...",
                    color = Color.White
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f),
                        color = Brown,
                        progress = { (progress * 0.01).toFloat() }
                    )
                    Text(
                        modifier = Modifier.padding(
                            start = 8.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        ),
                        color = Color.White,
                        text = "$progress%"
                    )
                }
            }
        }
    }
}