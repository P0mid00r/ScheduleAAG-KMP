package com.pomidorka.scheduleaag.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.ui.components.BackgroundCells
import com.pomidorka.scheduleaag.ui.components.schedule.ScheduleInteractive
import org.jetbrains.compose.resources.painterResource
import scheduleaag.composeapp.generated.resources.Res
import scheduleaag.composeapp.generated.resources.new_logo_vika

@Composable
fun ScheduleInteractiveScreenForStand(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = ::TopBar
    ) { paddings ->
        BackgroundCells(modifier.fillMaxSize()) {
            ScheduleInteractive(
                modifier = modifier
                    .padding(paddings)
                    .align(Alignment.Center),
                screenshotController = null,
            )
        }
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .dropShadow(
                shape = RoundedCornerShape(
                    bottomStart = 25.dp,
                    bottomEnd = 25.dp
                ),
                block = {
                    radius = 20f
                }
            )
            .clip(RoundedCornerShape(
                bottomStart = 25.dp,
                bottomEnd = 25.dp
            ))
            .fillMaxWidth()
            .background(Green)
            .statusBarsPadding()
            .height(200.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            Image(
                painter = painterResource(Res.drawable.new_logo_vika),
                contentDescription = null
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            BoxWithConstraints {
                val text = when {
                    maxWidth < 300.dp -> "Расписание"
                    maxWidth < 600.dp -> "Интерактивное расписание"
                    else -> "Интерактивное расписание\n«Алтайской академии гостеприимства»"
                }

                BasicText(
                    text = text,
                    color = { Color.White },
                    style = TextStyle.Default.copy(
                        textAlign = TextAlign.Center
                    ),
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 20.sp,
                        maxFontSize = 32.sp
                    ),
                    maxLines = if (maxWidth < 600.dp) 1 else 2,
                    modifier = Modifier.padding(15.dp)
                )
            }
        }
    }
}