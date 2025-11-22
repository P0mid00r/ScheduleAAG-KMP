package com.pomidorka.scheduleaag.ui.components.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pomidorka.scheduleaag.schedule.interactive.Schedule
import com.pomidorka.scheduleaag.schedule.old.ScheduleCall
import com.pomidorka.scheduleaag.ui.Brown

@Composable
fun ScheduleItem(
    schedule: Schedule,
    scheduleCalls: List<ScheduleCall>,
    isMonday: Boolean,
    modifier: Modifier = Modifier,
) {
    val scheduleCall = if (isMonday) {
        scheduleCalls[schedule.numberLesson]
    } else {
        if (schedule.numberLesson == 0) {
            scheduleCalls[0]
        } else {
            scheduleCalls[schedule.numberLesson - 1]
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(24.dp),
                )
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(24.dp))
                .background(Brown)
                .padding(12.dp)
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isMonday && schedule.numberLesson in listOf(0, 4)
                        && schedule.lesson.startsWith("Классный час")
                    ) {
                        "Кл. час"
                    } else {
                        val numberLesson = if (isMonday && schedule.numberLesson > 4) {
                            (schedule.numberLesson - 1)
                        } else {
                            schedule.numberLesson
                        }

                        "Пара $numberLesson"
                    },
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = Color.White,
                    )

                    Text(
                        text = scheduleCall.lessonTime,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
            HorizontalDivider(
                modifier = modifier
                    .padding(0.dp, 4.dp)
                    .clip(RoundedCornerShape(4.dp)),
                thickness = 4.dp,
                color = Color.White,
            )
            Column {
                Text(
                    text = "Группа: ${schedule.group}",
                    color = Color.White,
                )
                Spacer(modifier = modifier.height(4.dp))
                Text(
                    text = "Дисциплина: ${schedule.lesson}",
                    color = Color.White,
                )
                Spacer(modifier = modifier.height(4.dp))
                schedule.teacher.let {
                    Text(
                        text = if (it.contains(',')) {
                            "Преподаватели: $it"
                        } else {
                            "Преподаватель: $it"
                        },
                        color = Color.White,
                    )
                }
                Spacer(modifier = modifier.height(4.dp))
                schedule.classRoom.let {
                    Text(
                        text = if (it.contains(',')) {
                            "Аудитории: $it"
                        } else {
                            "Аудитория: $it"
                        },
                        color = Color.White,
                    )
                }
                Spacer(modifier = modifier.height(4.dp))
                Text(
                    text = "Корпус: ${schedule.territory}",
                    color = Color.White,
                )
            }
        }

        Spacer(modifier = modifier.height(8.dp))

        scheduleCall.interval.let {
            if (it != " ") {
                ScheduleCall(it)
            }
        }
    }
}