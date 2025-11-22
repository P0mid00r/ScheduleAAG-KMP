package com.pomidorka.scheduleaag.ui.components.alertdialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.CalendarDay
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.*
import com.kizitonwose.calendar.core.*
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.utils.DateTime.getDayOfWeekTrimName
import com.pomidorka.scheduleaag.utils.DateTime.getMonthName
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun CalendarDialog(
    visible: Boolean = true,
    date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    onDismiss: (() -> Unit)? = null,
    onDateSelected: ((LocalDate) -> Unit)? = null
) {
    if (visible) {
        val scope = rememberCoroutineScope()
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        var selectedDate by remember {
            mutableStateOf<LocalDate?>(
                if (date.dayOfWeek == DayOfWeek.SUNDAY) null else date
            )
        }
        var selectedYear by remember { mutableStateOf(date.year) }

        val startMonth = remember { YearMonth(selectedYear - 100, 1) }
        val endMonth = remember { YearMonth(selectedYear + 100, 12) }
        val currentMonth = remember { YearMonth(selectedYear, date.month.number) }

        val state = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = DayOfWeek.MONDAY
        )

        AlertDialog(
            title = { Text("Выберите дату") },
            titleContentColor = Green,
            confirmButton = {
                TextButton(
                    enabled = selectedDate != null,
                    onClick = {
                        selectedDate?.let { onDateSelected?.invoke(it) }
                    }
                ) {
                    Text(
                        text = "Выбрать",
                        color = if (selectedDate == null) Color.Gray else Green
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss?.invoke() }) {
                    Text("Отмена", color = Green)
                }
            },
            onDismissRequest = { onDismiss?.invoke() },
            properties = DialogProperties(
                dismissOnBackPress = false,
            ),
            text = {
                Column {
                    MonthHeader(
                        state = state,
                        onYearChange = {
                            scope.launch {
                                selectedYear = it
                                state.animateScrollToMonth(YearMonth(it, state.firstVisibleMonth.yearMonth.month))
                            }
                        }
                    )
                    HorizontalCalendar(
                        state = state,
                        dayContent = {
                            DayCell(
                                todayDay = today,
                                calendar = it,
                                isSelected = selectedDate == it.date,
                                onClick = {
                                    selectedDate = it.date
                                }
                            )
                        },
                        monthHeader = { month ->
                            val daysOfWeek = month.weekDays.first().map { it.date.dayOfWeek }
                            MonthHeader(daysOfWeek = daysOfWeek)
                        }
                    )
                }
            }
        )
    }
}

@Composable
private fun MonthHeader(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(dayOfWeek
                .getDayOfWeekTrimName()
                .substring(0, 2)
                .lowercase()
            )
        }
    }
}

@Composable
private fun DayCell(
    todayDay: LocalDate,
    calendar: CalendarDay,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isWeekend = calendar.date.dayOfWeek == DayOfWeek.SATURDAY ||
            calendar.date.dayOfWeek == DayOfWeek.SUNDAY

    val isToday = todayDay.day == calendar.date.day
            && todayDay.month == calendar.date.month
            && todayDay.year == calendar.date.year
    val todayBorderColor = if (isToday) Color.Blue else Color.Transparent
    val contentColor = if (isSelected) Color.White else {
        if (!isWeekend) Green else Color.Red
    }

    val backgroundColor = if (isSelected) Color.Blue else Color.Transparent

    if (calendar.position == DayPosition.MonthDate) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(24.dp))
                .border(width = 2.dp, color = todayBorderColor, shape = RoundedCornerShape(24.dp))
                .background(backgroundColor)
                .clickable(enabled = calendar.date.dayOfWeek != DayOfWeek.SUNDAY, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                color = contentColor,
                text = calendar.date.day.toString(),
            )
        }
    }
}

@Composable
private fun MonthHeader(
    state: CalendarState,
    onYearChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val month = state.firstVisibleMonth.yearMonth
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = {
            scope.launch {
                val month = month.minusMonths(1)
                state.animateScrollToMonth(month)
            }
        }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
        }
        TextButton(
            colors = ButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Unspecified,
                disabledContentColor = Color.Unspecified,
                disabledContainerColor = Color.Transparent
            ),
            onClick = {
                expanded = true
            }
        ) {
            val selectedYear = state.firstVisibleMonth.yearMonth.year
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                (selectedYear - 1..selectedYear + 1).forEach { year ->
                    DropdownMenuItem(text = { Text("$year") }, onClick = {
                        onYearChange(year)
                        expanded = false
                    })
                }
            }
            Text(
                textAlign = TextAlign.Center,
                text = "${month.month.getMonthName()} ${month.year}",
                style = MaterialTheme.typography.titleMedium
            )
        }
        IconButton(onClick = {
            scope.launch {
                val month = month.plusMonths(1)
                state.animateScrollToMonth(month)
            }
        }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
        }
    }
}
