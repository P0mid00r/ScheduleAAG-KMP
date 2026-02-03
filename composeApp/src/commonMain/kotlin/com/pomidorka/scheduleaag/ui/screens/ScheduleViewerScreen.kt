package com.pomidorka.scheduleaag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.pomidorka.scheduleaag.schedule.interactive.*
import com.pomidorka.scheduleaag.schedule.old.ScheduleApi
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.ui.components.BackgroundCells
import com.pomidorka.scheduleaag.ui.components.ShareFloatingActionButton
import com.pomidorka.scheduleaag.ui.components.TopAppBar
import com.pomidorka.scheduleaag.ui.components.alertdialogs.*
import com.pomidorka.scheduleaag.ui.components.schedule.ScheduleItem
import com.pomidorka.scheduleaag.utils.*
import com.pomidorka.scheduleaag.utils.DateTime.convertMillisToDate
import com.pomidorka.scheduleaag.utils.DateTime.convertMillisToDateRu
import com.pomidorka.scheduleaag.utils.DateTime.getDayNameInRussian
import com.pomidorka.scheduleaag.utils.DateTime.getMillisFromDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

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
    val screenshotController = rememberScreenshotController()
    val shareManager = rememberShareManager()
    val errorShareDialogController = ErrorDialogController {
        it.hideDialog()
    }
    val infoDialogController = InfoDialogController(message = "Расписание скопировано в буфер обмена!")

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
    val scroll = rememberScrollState()

    ErrorDialog(errorShareDialogController)
    InfoDialog(infoDialogController)
    LoadingDialog(loadingDialogController)
    ErrorDialog(errorDialogController)

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
        floatingActionButton = {
            if (scheduleList.isNotEmpty()) {
                ShareFloatingActionButton(
                    onClick = {
                        scope.launch {
                            val bitmap = screenshotController.captureAsync().await()
                            shareManager.shareImage(bitmap).let { result ->
                                if (!currentPlatform().type.isMobile) {
                                    result
                                        .onSuccess {
                                            infoDialogController.showDialog()
                                        }
                                        .onFailure { error ->
                                            errorShareDialogController.showDialog(error.message ?: "Произошла ошибка при копировании!")
                                        }
                                }
                            }
                        }
                    }
                )
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
                if (scheduleList.isNotEmpty()) {
                    val dayOfWeek = DateTime.getDayOfWeek(date)

                    val scheduleCalls = ScheduleApi.getScheduleCalls(dayOfWeek)
                    val isMonday = dayOfWeek == DayOfWeek.MONDAY

                    Column(
                        modifier = modifier
                            .padding(8.dp, 0.dp)
                            .weight(1f)
                            .verticalScroll(scroll)
                            .capturable(screenshotController)
                    ) {
                        scheduleList.forEach { schedule ->
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