package com.pomidorka.scheduleaag.ui.components.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.pomidorka.scheduleaag.Strings
import com.pomidorka.scheduleaag.data.SettingsData
import com.pomidorka.scheduleaag.schedule.Result
import com.pomidorka.scheduleaag.schedule.interactive.FilterData
import com.pomidorka.scheduleaag.schedule.interactive.FilterType
import com.pomidorka.scheduleaag.schedule.interactive.Schedule
import com.pomidorka.scheduleaag.schedule.interactive.ScheduleInteractiveApi
import com.pomidorka.scheduleaag.schedule.interactive.ScheduleInteractiveApi.getFilters
import com.pomidorka.scheduleaag.schedule.old.ScheduleApi
import com.pomidorka.scheduleaag.schedule.old.ScheduleCall
import com.pomidorka.scheduleaag.ui.Brown
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.ui.components.ExpandingSearchFiltersScreen
import com.pomidorka.scheduleaag.ui.components.alertdialogs.*
import com.pomidorka.scheduleaag.utils.DateTime
import com.pomidorka.scheduleaag.utils.DateTime.convertMillisToDate
import com.pomidorka.scheduleaag.utils.DateTime.convertMillisToDateRu
import com.pomidorka.scheduleaag.utils.DateTime.getLocalDateFromMillis
import com.pomidorka.scheduleaag.utils.DateTime.getMillisFromDate
import com.pomidorka.scheduleaag.utils.Log
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek

@Composable
fun ScheduleInteractive(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val loadingDialogController = LoadingDialogController(
        message = Strings.PROGRESS_DIALOG_SCHEDULE
    )
    val errorDialogController = ErrorDialogController(
        onConfirm = { it.hideDialog() }
    )
    val fatalErrorDialogController = ErrorDialogController(
        onConfirm = {
            it.hideDialog()
            navController.popBackStack()
        }
    )

    LoadingDialog(loadingDialogController)
    ErrorDialog(errorDialogController)
    ErrorDialog(fatalErrorDialogController)

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var isShowDatePicker by rememberSaveable { mutableStateOf(false) }

    var filterType by rememberSaveable { mutableStateOf(FilterType.Group) }
    var scheduleList by remember { mutableStateOf(emptyList<Schedule>()) }
    var loadedFilters by remember { mutableStateOf(emptyList<FilterData>()) }
    var selectedDateMillis by rememberSaveable { mutableLongStateOf(DateTime.getCurrentMillis()) }
    var selectedFilter by remember { mutableStateOf<FilterData?>(null) }

    suspend fun loadFiltersAtType(filterType: FilterType) {
        selectedFilter = null

        val result = filterType.getFilters()

        loadedFilters = when(result) {
            is Result.Success -> {
                result.data.apply {
                    if (isNotEmpty()) {
                        selectedFilter = first()
                    } else {
                        fatalErrorDialogController.showDialog("Ошибка при загрузке списка фильтров, попробуйте позже!")
                    }
                }
            }
            is Result.Failure -> {
                Log.error(
                    tag = "ScheduleInteractive.kt",
                    throwable = result.throwable
                )
                result.throwable.message?.let {
                    fatalErrorDialogController.showDialog(it)
                }

                emptyList()
            }
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            loadFiltersAtType(filterType)

            selectedFilter = if (loadedFilters.isNotEmpty()) {
                if (SettingsData.selectedGroup == null) {
                    loadedFilters.first()
                } else SettingsData.selectedGroup
            } else null
        }
    }

    if (isExpanded) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            ExpandingSearchFiltersScreen(
                filtersList = loadedFilters,
                onExpandedChanged = {
                    isExpanded = it
                },
                onSelectedItem = {
                    selectedFilter = it
                    scheduleList = emptyList()
                }
            )
        }
    }

    CalendarDialog(
        visible = isShowDatePicker,
        date = selectedDateMillis.getLocalDateFromMillis(),
        onDateSelected = {
            selectedDateMillis = it.getMillisFromDate()
            isShowDatePicker = false
        },
        onDismiss = {
            isShowDatePicker = false
        }
    )

    Column(
        modifier = modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .padding(12.dp, 0.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FilterButtons(
            onGroupFilterClick = {
                filterType = FilterType.Group
                scope.launch {
                    loadFiltersAtType(filterType)
                }
            },
            onPrepFilterClick = {
                filterType = FilterType.Prep
                scope.launch {
                    loadFiltersAtType(filterType)
                }
            },
            onAudFilterClick = {
                filterType = FilterType.Aud
                scope.launch {
                    loadFiltersAtType(filterType)
                }
            }
        )

        Spacer(Modifier.height(12.dp))

        ScheduleSelector(
            selectedFilter = selectedFilter,
            date = selectedDateMillis.convertMillisToDateRu(),
            onFilterSelectClick = {
                isExpanded = true
            },
            onDateSelectClick = {
                isShowDatePicker = !isShowDatePicker
            },
            onLoadScheduleClick = {
                loadingDialogController.showDialog()

                scope.launch {
                    if (selectedFilter != null) {
                        val result = ScheduleInteractiveApi.getScheduleAtFilterType(
                            filterType = filterType,
                            filterData = selectedFilter!!,
                            calendar = selectedDateMillis.convertMillisToDate()
                        )

                        loadingDialogController.hideDialog()

                        scheduleList = when(result) {
                            is Result.Success -> {
                                if (result.data.isEmpty()) {
                                    errorDialogController.showDialog("Расписания на выбранную дату нет!")
                                }

                                Log.info("ScheduleInteractive.kt") {
                                    "Интерактивное расписание\nФильтр: ${selectedFilter?.data}\nДата: ${selectedDateMillis.convertMillisToDateRu()}"
                                }

                                result.data
                            }

                            is Result.Failure -> {
                                Log.error(
                                    tag = "ScheduleInteractive.kt",
                                    throwable = result.throwable
                                )
                                result.throwable.message?.let { message ->
                                    fatalErrorDialogController.showDialog(message)
                                }

                                emptyList()
                            }
                        }
                    } else {
                        loadingDialogController.hideDialog()
                        errorDialogController.showDialog("Вы не выбрали фильтр!")
                    }
                }
            }
        )

        Spacer(Modifier.height(12.dp))

        if (scheduleList.isNotEmpty()) {
            val dayOfWeek = DateTime.getDayOfWeek(selectedDateMillis)

            val scheduleCalls = ScheduleApi.getScheduleCalls(dayOfWeek)
            val isMonday = dayOfWeek == DayOfWeek.MONDAY

            ScheduleList(
                scheduleList = scheduleList,
                scheduleCalls = scheduleCalls,
                isMonday = isMonday
            )
        }
    }
}

@Composable
private fun FilterButtons(
    onGroupFilterClick: () -> Unit,
    onPrepFilterClick: () -> Unit,
    onAudFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonColors = ButtonDefaults.buttonColors(
        contentColor = Color.White,
        containerColor = Green
    )
    val buttonModifier = modifier.width(200.dp)

    Column {
        Button(
            modifier = buttonModifier,
            onClick = onGroupFilterClick,
            colors = buttonColors
        ) {
            Text(text = "По группе")
        }
        Button(
            modifier = buttonModifier,
            onClick = onPrepFilterClick,
            colors = buttonColors
        ) {
            Text(text = "По преподавателю")
        }
        Button(
            modifier = buttonModifier,
            onClick = onAudFilterClick,
            colors = buttonColors
        ) {
            Text(text = "По аудитории")
        }
    }
}

@Composable
private fun ScheduleSelector(
    date: String,
    selectedFilter: FilterData?,
    onFilterSelectClick: () -> Unit,
    onDateSelectClick: () -> Unit,
    onLoadScheduleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(24.dp),
            )
            .clip(RoundedCornerShape(24.dp))
            .background(Brown)
            .padding(8.dp)
    ) {
        Button(
            modifier = modifier.fillMaxWidth(),
            onClick = onFilterSelectClick,
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Green
            )
        ) {
            Text(text = selectedFilter?.data ?: "Идет загрузка...")
        }
        Button(
            modifier = modifier.fillMaxWidth(),
            onClick = onDateSelectClick,
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Green
            )
        ) {
            Text(text = date)
        }
        Button(
            modifier = modifier
                .fillMaxWidth()
                .padding(0.dp, 12.dp),
            onClick = onLoadScheduleClick,
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Green
            )
        ) {
            Text(text = "Выбрать")
        }
    }
}

@Composable
fun ScheduleList(
    scheduleList: List<Schedule>,
    scheduleCalls: List<ScheduleCall>,
    isMonday: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier.padding(0.dp, 12.dp)) {
        for (scheduleData in scheduleList) {
            ScheduleItem(
                schedule = scheduleData,
                scheduleCalls = scheduleCalls,
                isMonday = isMonday
            )

            Spacer(modifier.height(12.dp))
        }
    }
}