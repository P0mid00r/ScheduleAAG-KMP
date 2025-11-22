package com.pomidorka.scheduleaag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pomidorka.scheduleaag.Strings
import com.pomidorka.scheduleaag.ad.AdManager
import com.pomidorka.scheduleaag.data.SettingsData
import com.pomidorka.scheduleaag.schedule.Result
import com.pomidorka.scheduleaag.schedule.interactive.FilterData
import com.pomidorka.scheduleaag.schedule.interactive.FilterType
import com.pomidorka.scheduleaag.schedule.interactive.Schedule
import com.pomidorka.scheduleaag.schedule.interactive.ScheduleInteractiveApi
import com.pomidorka.scheduleaag.schedule.interactive.ScheduleType
import com.pomidorka.scheduleaag.schedule.old.ScheduleApi
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.ui.components.BackgroundCells
import com.pomidorka.scheduleaag.ui.components.TopAppBar
import com.pomidorka.scheduleaag.ui.components.alertdialogs.ErrorDialog
import com.pomidorka.scheduleaag.ui.components.alertdialogs.ErrorDialogController
import com.pomidorka.scheduleaag.ui.components.alertdialogs.LoadingDialog
import com.pomidorka.scheduleaag.ui.components.alertdialogs.LoadingDialogController
import com.pomidorka.scheduleaag.ui.components.schedule.ScheduleItem
import com.pomidorka.scheduleaag.utils.DateTime
import com.pomidorka.scheduleaag.utils.DateTime.convertMillisToDate
import com.pomidorka.scheduleaag.utils.DateTime.convertMillisToDateRu
import com.pomidorka.scheduleaag.utils.DateTime.getDayNameInRussian
import com.pomidorka.scheduleaag.utils.DateTime.getMillisFromDate
import com.pomidorka.scheduleaag.utils.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.datetime.LocalDate

private var scheduleList by mutableStateOf(emptyList<Schedule>())
private var date by mutableLongStateOf(DateTime.getCurrentMillis())
private lateinit var messageNotSchedule: String
private lateinit var errorDialogController: ErrorDialogController
private val loadingDialogController = LoadingDialogController(
    message = Strings.PROGRESS_DIALOG_SCHEDULE
)

@Composable
fun ScheduleViewerScreen(
    filterType: FilterType,
    scheduleType: ScheduleType,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    messageNotSchedule = "На ${when(scheduleType) {
        ScheduleType.Today -> "сегодня"
        ScheduleType.NextDay -> "следующий ближайший день"
    }} нету расписания!"
    errorDialogController = ErrorDialogController(
        onConfirm = {
            it.hideDialog()
            navController.popBackStack()
        }
    )

    val scope = rememberCoroutineScope()

    LoadingDialog(loadingDialogController)
    ErrorDialog(errorDialogController)

    Scaffold(
        topBar = {
            Column(
                modifier = modifier
                    .dropShadow(
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp
                        ),
                        block = {
                            radius = 20f
                        }
                    )
            ) {
                TopAppBar(
                    title = "Расписание ${when(filterType) {
                        FilterType.Group -> SettingsData.selectedGroup?.data
                        FilterType.Prep -> ScheduleInteractiveApi.trimNameTeacher(SettingsData.selectedTeacher?.data!!)
                        FilterType.Aud -> throw NotImplementedError()
                    }}",
                    onBackClick = {
                        navController.popBackStack()
                    }
                )

                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp
                        ))
                        .background(Green),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${date.getDayNameInRussian()} ${date.convertMillisToDateRu()}",
                        color = Color.White
                    )
                }
            }
        },
        bottomBar = {
            AdManager.AdBannerScheduleScreen(
                modifier = Modifier
                    .background(Green)
                    .navigationBarsPadding(),
                backgroundColor = Green
            )
        }
    ) { paddings ->
        BackgroundCells {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddings),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LaunchedEffect(Unit) {
                    date = DateTime.getCurrentMillis()
                    scheduleList = emptyList()
                    loadingDialogController.showDialog()
                    loadSchedule(
                        coroutineScope = scope,
                        filterType = filterType,
                        scheduleType = scheduleType,
                        filterData = if (filterType == FilterType.Group) {
                            SettingsData.selectedGroup!!
                        } else {
                            SettingsData.selectedTeacher!!
                        }
                    )
                }

                if (scheduleList.isNotEmpty()) {
                    val dayOfWeek = DateTime.getDayOfWeek(date)

                    val scheduleCalls = ScheduleApi.getScheduleCalls(dayOfWeek)
                    val isMonday = dayOfWeek == DayOfWeek.MONDAY

                    LazyColumn(
                        modifier = modifier
                            .padding(8.dp, 0.dp)
                            .weight(1f)
                    ) {
                        items(scheduleList) { schedule ->
                            Spacer(modifier = modifier.height(8.dp))
                            ScheduleItem(
                                modifier = modifier.widthIn(max = 500.dp),
                                schedule = schedule,
                                scheduleCalls = scheduleCalls,
                                isMonday = isMonday
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun loadSchedule(
    coroutineScope: CoroutineScope,
    filterType: FilterType,
    scheduleType: ScheduleType,
    filterData: FilterData
) {
    when (scheduleType) {
        ScheduleType.Today -> {
            loadScheduleToday(filterType, filterData, coroutineScope)
        }

        ScheduleType.NextDay -> {
            loadScheduleNextDay(filterType, filterData, coroutineScope)
        }
    }
}

private fun loadScheduleToday(
    filterType: FilterType,
    filterData: FilterData,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        ScheduleInteractiveApi.getScheduleAtFilterType(
            filterType = filterType,
            filterData = filterData,
            calendar = date.convertMillisToDate()
        ).let { result ->
            when(result) {
                is Result.Success -> {
                    scheduleList = result.data

                    Log.info("ScheduleViewerScreen.kt") {
                        "Новое расписание на сегодня\nТип фильтра: ${filterType.name}\nФильтр: ${filterData.data}\nДата: ${date.convertMillisToDateRu()}"
                    }

                    loadingDialogController.hideDialog()
                    if (scheduleList.isEmpty()) {
                        errorDialogController.showDialog(messageNotSchedule)
                    }
                }

                is Result.Failure -> {
                    loadingDialogController.hideDialog()
                    result.throwable.message?.let {
                        errorDialogController.showDialog(it)
                    }
                }
            }
        }
    }
}

private fun loadScheduleNextDay(
    filterType: FilterType,
    filterData: FilterData,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        var currentDate = LocalDate.parse(DateTime.getCurrentMillis().convertMillisToDate())
        var attempt = 0

        while (isActive) {
            attempt++
            currentDate += DatePeriod(days = 1)
            currentDate.day.plus(1)

            val selectedMillis = currentDate.getMillisFromDate()

            val result = ScheduleInteractiveApi.getScheduleAtFilterType(
                filterType = filterType,
                filterData = filterData,
                calendar = selectedMillis.convertMillisToDate()
            )

            when(result) {
                is Result.Success -> {
                    scheduleList = result.data

                    if (scheduleList.isNotEmpty()) {
                        date = selectedMillis
                        Log.info("ScheduleViewerScreen.kt") {
                            "Новое расписание на следующий день\nТип фильтра: ${filterType.name}\nФильтр: ${filterData.data}\nДата: ${date.convertMillisToDateRu()}"
                        }
                        loadingDialogController.hideDialog()
                        break
                    }

                    if (attempt >= 10) {
                        loadingDialogController.hideDialog()
                        errorDialogController.showDialog(messageNotSchedule)
                        break
                    }
                }

                is Result.Failure -> {
                    loadingDialogController.hideDialog()
                    result.throwable.message?.let {
                        errorDialogController.showDialog(it)
                    }
                    break
                }
            }
        }
    }
}