package com.pomidorka.scheduleaag.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pomidorka.scheduleaag.data.SettingsData
import com.pomidorka.scheduleaag.data.SettingsRepository
import com.pomidorka.scheduleaag.schedule.TypeView
import com.pomidorka.scheduleaag.schedule.interactive.FilterType
import com.pomidorka.scheduleaag.schedule.interactive.ScheduleInteractiveApi
import com.pomidorka.scheduleaag.schedule.old.CollegeBuilding
import com.pomidorka.scheduleaag.ui.Brown
import com.pomidorka.scheduleaag.ui.Green
import com.pomidorka.scheduleaag.ui.components.BackgroundCells
import com.pomidorka.scheduleaag.ui.components.ExpandingSearchFiltersScreen
import com.pomidorka.scheduleaag.ui.components.TopAppBar
import com.pomidorka.scheduleaag.utils.getVibrator

@Composable
fun SettingsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val vibrator = getVibrator()
    val scroll = rememberScrollState()
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var filterType: FilterType by rememberSaveable { mutableStateOf(FilterType.Group) }

    AnimatedContent(
        modifier = modifier.fillMaxSize(),
        targetState = isExpanded,
        label = "selectScreenSearch"
    ) { expanded ->
        if (expanded) {
            ExpandingSearchFiltersScreen(
                modifier = modifier.fillMaxSize(),
                filterType = filterType,
                onExpandedChanged = {
                    isExpanded = it
                },
                onSelectedItem = {
                    when (filterType) {
                        FilterType.Group -> {
                            SettingsRepository.selectedGroup = it
                            SettingsData.selectedGroup = it
                        }

                        FilterType.Prep -> {
                            SettingsRepository.selectedTeacher = it
                            SettingsData.selectedTeacher = it
                        }

                        FilterType.Aud -> TODO()
                    }
                }
            )
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = "Настройки",
                        onBackClick = { navController.popBackStack() }
                    )
                }
            ) { paddings ->
                BackgroundCells {
                    Column(
                        modifier = modifier
                            .padding(paddings)
                            .fillMaxSize()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 0.dp
                            )
                            .verticalScroll(scroll),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TypeViewSetting(
                            onFilterClick = {
                                filterType = it
                                isExpanded = true
                            }
                        )

                        if (vibrator.isVibrateSupported) {
                            CardContainer {
                                ItemSetting(title = "Вибрация: ") {
                                    Switch(
                                        colors = SwitchDefaults.colors(
                                            checkedTrackColor = Green,
                                            checkedThumbColor = Color.White,
                                        ),
                                        checked = SettingsData.isVibrationActive,
                                        onCheckedChange = {
                                            SettingsRepository.isVibrationActive = it
                                            SettingsData.isVibrationActive = it
                                            vibrator.vibrateClick()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TypeViewSetting(onFilterClick: (FilterType) -> Unit) {
    var selectedTypeView: TypeView by mutableStateOf(
        if (SettingsData.isNewSchedule) TypeView.New else TypeView.Old
    )

    CardContainer {
        ItemSetting(title = "Вид расписания") {
            Box {
                var expandedDropDownMenu by remember { mutableStateOf(false) }

                Button(
                    onClick = {
                        expandedDropDownMenu = true
                    },
                    elevation = ButtonDefaults.buttonElevation(pressedElevation = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green,
                        contentColor = Color.White
                    ),
                ) {
                    Text(
                        text = selectedTypeView.value,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                DropdownMenu(
                    expanded = expandedDropDownMenu,
                    onDismissRequest = { expandedDropDownMenu = false },
                    containerColor = Color.White
                ) {
                    DropdownMenuItem(
                        text = { Text(TypeView.New.value) },
                        onClick = {
                            selectedTypeView = TypeView.New
                            SettingsRepository.isNewSchedule = true
                            SettingsData.isNewSchedule = true
                            expandedDropDownMenu = false
                        },
                    )

                    DropdownMenuItem(
                        text = { Text(TypeView.Old.value) },
                        onClick = {
                            selectedTypeView = TypeView.Old
                            SettingsRepository.isNewSchedule = false
                            SettingsData.isNewSchedule = false
                            expandedDropDownMenu = false
                        },
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color.White,
            thickness = 4.dp,
        )

        AnimatedContent(
            targetState = selectedTypeView,
            transitionSpec = {
                expandVertically() togetherWith shrinkVertically()
            }
        ) { typeView ->
            Column {
                OldItemSetting()
                when(typeView) {
                    TypeView.New -> {
                        NewItemSetting(onFilterClick = onFilterClick)
                    }

                    TypeView.Old -> {}
                }
            }
        }
    }
}

@Composable
private fun NewItemSetting(onFilterClick: (FilterType) -> Unit) {
    ItemSetting(title = "Тип расписания: ") {
        var expandedDropDownMenu by remember { mutableStateOf(false) }
        val filterNames = mapOf(
            FilterType.Group to "По группе",
            FilterType.Prep to "По преподавателю",
        )

        Box {
            Button(
                onClick = {
                    expandedDropDownMenu = true
                },
                elevation = ButtonDefaults.buttonElevation(pressedElevation = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green,
                    contentColor = Color.White
                ),
            ) {
                Text(
                    text = filterNames[SettingsData.selectedTypeFilter]!!,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            DropdownMenu(
                expanded = expandedDropDownMenu,
                onDismissRequest = { expandedDropDownMenu = false },
                containerColor = Color.White
            ) {
                for (filterName in filterNames) {
                    DropdownMenuItem(
                        text = { Text(filterName.value) },
                        onClick = {
                            expandedDropDownMenu = false
                            SettingsRepository.selectedTypeFilter = filterName.key
                            SettingsData.selectedTypeFilter = filterName.key
                        },
                    )
                }
            }
        }
    }

    Crossfade(
        targetState = SettingsData.selectedTypeFilter,
        animationSpec = tween()
    ) { filterType ->
        when(filterType) {
            FilterType.Group -> {
                ItemSetting(title = "Группа: ") {
                    Button(
                        elevation = ButtonDefaults.buttonElevation(pressedElevation = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green,
                            contentColor = Color.White
                        ),
                        onClick = {
                            onFilterClick(FilterType.Group)
                        }
                    ) {
                        Text(
                            text = if (SettingsData.selectedGroup == null) {
                                "Выбрать группу"
                            } else {
                                SettingsData.selectedGroup!!.data
                            }
                        )
                    }
                }
            }

            FilterType.Prep -> {
                ItemSetting(title = "Преподаватель: ") {
                    Button(
                        elevation = ButtonDefaults.buttonElevation(pressedElevation = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green,
                            contentColor = Color.White
                        ),
                        onClick = {
                            onFilterClick(FilterType.Prep)
                        }
                    ) {
                        Text(
                            text = if (SettingsData.selectedTeacher == null) {
                                "Выбрать фио"
                            } else {
                                ScheduleInteractiveApi.trimNameTeacher(SettingsData.selectedTeacher!!.data)
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            FilterType.Aud -> TODO()
        }
    }
}

@Composable
private fun OldItemSetting() {
    ItemSetting(title = "Корпус: ") {
        Box {
            var expandedDropDownMenu by remember { mutableStateOf(false) }

            Button(
                onClick = {
                    expandedDropDownMenu = true
                },
                elevation = ButtonDefaults.buttonElevation(pressedElevation = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green,
                    contentColor = Color.White
                ),
            ) {
                Text(
                    text = SettingsData.selectedCollegeBuilding.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            DropdownMenu(
                expanded = expandedDropDownMenu,
                onDismissRequest = { expandedDropDownMenu = false },
                containerColor = Color.White
            ) {
                for (collegeBuilding in CollegeBuilding.entries) {
                    DropdownMenuItem(
                        text = { Text(collegeBuilding.name) },
                        onClick = {
                            SettingsData.selectedCollegeBuilding = collegeBuilding
                            SettingsRepository.selectedCollegeBuilding = collegeBuilding
                            expandedDropDownMenu = false
                        },
                    )
                }
            }
        }
    }
}


@Composable
private inline fun CardContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
//    Card(
//        modifier = modifier
//            .widthIn(max = 500.dp)
//            .padding(vertical = 4.dp),
//        elevation = CardDefaults.elevatedCardElevation(
//            defaultElevation = 4.dp
//        ),
//        shape = RoundedCornerShape(24.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = Brown
//        )
//    ) {
//        Column(Modifier.padding(16.dp)) {
//            content()
//        }
//    }


    Box(
        modifier = modifier
            .widthIn(max = 500.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(24.dp),
            )
            .padding(0.dp, 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brown)
    ) {
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            content()
        }
    }


//    Column(
//        modifier = modifier
//            .shadow(
//                elevation = 4.dp,
//                shape = RoundedCornerShape(24.dp),
//            )
//            .widthIn(max = 500.dp)
//            .padding(0.dp, 4.dp)
//            .clip(RoundedCornerShape(24.dp))
//            .background(Brown)
//            .padding(16.dp)
//    ) {
//        content()
//    }
}

@Composable
private inline fun ItemSetting(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = Color.White
        )
        content()
    }
}