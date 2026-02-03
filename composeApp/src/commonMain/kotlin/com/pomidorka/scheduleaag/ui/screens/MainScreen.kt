package com.pomidorka.scheduleaag.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pomidorka.scheduleaag.Strings
import com.pomidorka.scheduleaag.ad.AdManager
import com.pomidorka.scheduleaag.data.SettingsData
import com.pomidorka.scheduleaag.schedule.Result
import com.pomidorka.scheduleaag.schedule.interactive.FilterType
import com.pomidorka.scheduleaag.schedule.interactive.ScheduleType
import com.pomidorka.scheduleaag.schedule.old.ScheduleApi
import com.pomidorka.scheduleaag.schedule.old.ScheduleApi.toUrl
import com.pomidorka.scheduleaag.ui.Brown
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.ui.components.BackgroundCells
import com.pomidorka.scheduleaag.ui.components.IconButton
import com.pomidorka.scheduleaag.ui.components.NavigationBar
import com.pomidorka.scheduleaag.ui.components.alertdialogs.*
import com.pomidorka.scheduleaag.ui.navigation.Route
import com.pomidorka.scheduleaag.utils.Log
import com.pomidorka.scheduleaag.utils.getVibrator
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import scheduleaag.composeapp.generated.resources.Res
import scheduleaag.composeapp.generated.resources.info
import scheduleaag.composeapp.generated.resources.new_logo_vika
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun MainScreen(navController: NavHostController) {
    Log.info("MainScreen.kt") { "Переход на главный экран" }

    Scaffold(
        topBar = {
            LogoBar()
        },
        bottomBar = {
            Column {
                BottomBar()

                AdManager.AdBannerMainScreen(
                    backgroundColor = Brown
                )

                NavigationBar(Brown)
            }
        }
    ) { paddings ->
        BackgroundCells {
            Column(
                modifier = Modifier.padding(paddings),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    ButtonsPanel(
                        navController = navController,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .widthIn(max = 600.dp),
                    )

                    BottomButtonsBar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter),
                        navController = navController,
                    )
                }
            }
        }
    }
}

@Composable
private fun LogoBar() {
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
                    maxWidth < 600.dp -> "Расписание"
                    else -> "Расписание «Алтайской академии гостеприимства»"
                }

                BasicText(
                    text = text,
                    color = { Color.White },
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 20.sp,
                        maxFontSize = 32.sp
                    ),
                    maxLines = 1,
                    modifier = Modifier.padding(15.dp)
                )
            }
        }
    }
}

private fun isSelectedFilterData(filterType: FilterType): Boolean {
    return when(filterType) {
        FilterType.Group -> SettingsData.selectedGroup != null
        FilterType.Prep -> SettingsData.selectedTeacher != null
        else -> false
    }
}

@Composable
private fun ButtonsPanel(
    navController: NavHostController,
    modifier: Modifier
) {
    val scope = rememberCoroutineScope()
    val loadingDialog = LoadingDialogController(Strings.PROGRESS_DIALOG_LOADING_PAGE)
    val errorAlertDialogController = ErrorDialogController(
        onConfirm = {
            it.hideDialog()
        }
    )

    LoadingDialog(loadingDialog)
    ErrorDialog(errorAlertDialogController)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
    ) {
        Row(Modifier.fillMaxWidth()) {
            Button(
                text = "На сегодня",
                modifier = Modifier.weight(1f),
                onClick = {
                    if (SettingsData.isNewSchedule) {
                        val isSelected = isSelectedFilterData(SettingsData.selectedTypeFilter)

                        if (isSelected) {
                            Log.info("MainScreen.kt") { "Переход в просмотр расписания" }

                            navController.navigate(
                                Route.ScheduleViewerScreen(
                                    filterTypeName = SettingsData.selectedTypeFilter.name,
                                    scheduleTypeName = ScheduleType.Today.name
                                )
                            )
                        } else {
                            val message = when (SettingsData.selectedTypeFilter) {
                                FilterType.Group -> "Вы не выбрали группу!"
                                FilterType.Prep -> "Вы не выбрали фио!"
                                else -> throw NotImplementedError()
                            }

                            errorAlertDialogController.showDialog(message)
                        }
                    } else {
                        scope.launch {
                            loadingDialog.showDialog()

                            val result = ScheduleApi.parseScheduleTodayUrl(
                                SettingsData.selectedCollegeBuilding.toUrl()
                            )

                            loadingDialog.hideDialog()

                            when(result) {
                                is Result.Success -> {
                                    Log.info("MainScreen.kt") { "Переход в просмотр pdf по url: ${result.data}" }
                                    navController.navigate(Route.SchedulePdfViewerScreen(result.data))
                                }
                                is Result.Failure -> {
                                    errorAlertDialogController.showDialog(result.throwable.message!!)
                                }
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.padding(5.dp))

            Button(
                text = "На завтра",
                modifier = Modifier.weight(1f),
                onClick = {
                    if (SettingsData.isNewSchedule) {
                        val isSelected = isSelectedFilterData(SettingsData.selectedTypeFilter)

                        if (isSelected) {
                            Log.info("MainScreen.kt") { "Переход в просмотр расписания" }

                            navController.navigate(
                                Route.ScheduleViewerScreen(
                                    filterTypeName = SettingsData.selectedTypeFilter.name,
                                    scheduleTypeName = ScheduleType.NextDay.name
                                )
                            )
                        } else {
                            val message = when (SettingsData.selectedTypeFilter) {
                                FilterType.Group -> "Вы не выбрали группу!"
                                FilterType.Prep -> "Вы не выбрали фио!"
                                else -> throw NotImplementedError()
                            }

                            errorAlertDialogController.showDialog(message)
                        }
                    } else {
                        scope.launch {
                            loadingDialog.showDialog()

                            val result = ScheduleApi.parseScheduleNextDayUrl(
                                SettingsData.selectedCollegeBuilding.toUrl()
                            )

                            loadingDialog.hideDialog()

                            when(result) {
                                is Result.Success -> {
                                    Log.info("MainScreen.kt") { "Переход в просмотр pdf по url: ${result.data}" }
                                    navController.navigate(Route.SchedulePdfViewerScreen(result.data))
                                }
                                is Result.Failure -> {
                                    errorAlertDialogController.showDialog(result.throwable.message!!)
                                }
                            }
                        }
                    }
                }
            )
        }

        Spacer(Modifier.padding(5.dp))

        Button(
            text = "Интерактивное расписание",
            onClick = {
                Log.info("MainScreen.kt") { "Переход в интерактивное расписание" }
                navController.navigate(Route.ScheduleInteractiveScreen)
            },
            modifier = Modifier
        )

        Spacer(Modifier.padding(5.dp))

        Button(
            text = "Выбрать день",
            onClick = {
                Log.info("MainScreen.kt") { "Переход в выбор дня с выбранным корпусом '${SettingsData.selectedCollegeBuilding.name}'" }
                navController.navigate(Route.ScheduleDaysSelectorScreen)
            },
            modifier = Modifier
        )

        Spacer(Modifier.padding(5.dp))

        Button(
            text = "Расписание звонков",
            onClick = {
                Log.info("MainScreen.kt") { "Переход в расписание звонков" }
                navController.navigate(Route.CallsScreen)
            },
            modifier = Modifier
        )
    }
}

@Composable
private fun Button(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    val vibrator = getVibrator()

    Button(
        modifier = modifier
            .dropShadow(
                shape = ButtonDefaults.shape,
                block = {
                    radius = 10f
                }
            )
            .fillMaxWidth()
            .height(50.dp),
        onClick = {
            onClick()
            vibrator.vibrateClick()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Green,
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
        )
    ) {
        Text(text)
    }
}

@Composable
private fun BottomButtonsBar(
    modifier: Modifier,
    navController: NavHostController
) {
    var isShowInfoDialog by rememberSaveable { mutableStateOf(false) }

    AboutAppDialog(
        visible = isShowInfoDialog,
        onBackClick = { isShowInfoDialog = false }
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = {
                Log.info("MainScreen.kt") { "Переход в настройки" }
                navController.navigate(Route.SettingsScreen)
            },
            imageVector = Icons.Filled.Settings,
            background = Green,
            modifier = Modifier
                .size(80.dp)
                .padding(
                    start = 12.dp,
                    bottom = 12.dp
                ),
        )

        IconButton(
            onClick = { isShowInfoDialog = true },
            painter = painterResource(Res.drawable.info),
            background = Green,
            modifier = Modifier
                .size(80.dp)
                .padding(
                    end = 12.dp,
                    bottom = 12.dp
                ),
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun BottomBar() {
    val year = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year

    Row(
        modifier = Modifier
            .dropShadow(
                shape = RoundedCornerShape(
                    topStart = 25.dp,
                    topEnd = 25.dp
                ),
                block = {
                    radius = 20f
                }
            )
            .clip(RoundedCornerShape(
                topStart = 25.dp,
                topEnd = 25.dp
            ))
            .background(Brown)
            .fillMaxWidth()
            .height(70.dp)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        BasicText(
            autoSize = TextAutoSize.StepBased(
                minFontSize = 10.sp,
                maxFontSize = 20.sp
            ),
            text = "КГБПОУ \"Алтайская академия гостеприимства\" - $year",
            color = { Color.White },
            maxLines = 1,
        )
    }
}