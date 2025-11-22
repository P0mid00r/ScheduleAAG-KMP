package com.pomidorka.scheduleaag.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pomidorka.scheduleaag.schedule.interactive.FilterData
import com.pomidorka.scheduleaag.schedule.interactive.FilterType
import com.pomidorka.scheduleaag.schedule.old.CollegeBuilding
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import com.russhwolf.settings.set
import kotlinx.serialization.ExperimentalSerializationApi

object SettingsData {
    var isNewSchedule by mutableStateOf(true)
    var isVibrationActive by mutableStateOf(true)
    var selectedGroup: FilterData? by mutableStateOf(null)
    var selectedTeacher: FilterData? by mutableStateOf(null)
    var selectedTypeFilter: FilterType by mutableStateOf(FilterType.Group)
    var selectedCollegeBuilding: CollegeBuilding by mutableStateOf(CollegeBuilding.First)
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
object SettingsRepository {
    private val settings: Settings = Settings()

    private const val IS_VIBRATION_ACTIVE_KEY = "IS_VIBRATION_ACTIVE_KEY"
    private const val IS_NEW_SCHEDULE_KEY = "IS_NEW_SCHEDULE_KEY"
    private const val SELECTED_GROUP_KEY = "SELECTED_GROUP_KEY"
    private const val SELECTED_TEACHER_KEY = "SELECTED_TEACHER_KEY"
    private const val SELECTED_FILTER_TYPE_KEY = "SELECTED_FILTER_TYPE_KEY"
    private const val SELECTED_COLLEGE_BUILDING_KEY = "SELECTED_COLLEGE_BUILDING_KEY"

    fun initialization() {
        SettingsData.isVibrationActive = isVibrationActive
        SettingsData.selectedTypeFilter = selectedTypeFilter
        SettingsData.selectedGroup = selectedGroup
        SettingsData.selectedTeacher = selectedTeacher
        SettingsData.isNewSchedule = isNewSchedule
        SettingsData.selectedCollegeBuilding = selectedCollegeBuilding
    }

    var isVibrationActive
        get() = settings.getBoolean(IS_VIBRATION_ACTIVE_KEY, true)
        set(value) = settings.putBoolean(IS_VIBRATION_ACTIVE_KEY, value)

    var isNewSchedule
        get() = settings.getBoolean(IS_NEW_SCHEDULE_KEY, true)
        set(value) = settings.set(IS_NEW_SCHEDULE_KEY, value)

    var selectedGroup
        get() = settings.decodeValueOrNull(FilterData.serializer(), SELECTED_GROUP_KEY)
        set(value) = settings.encodeValue(FilterData.serializer(), SELECTED_GROUP_KEY, value!!)

    var selectedTeacher
        get() = settings.decodeValueOrNull(FilterData.serializer(), SELECTED_TEACHER_KEY)
        set(value) = settings.encodeValue(FilterData.serializer(), SELECTED_TEACHER_KEY, value!!)

    var selectedTypeFilter
        get() = FilterType.valueOf(settings.getString(SELECTED_FILTER_TYPE_KEY, FilterType.Group.name))
        set(value) = settings.set(SELECTED_FILTER_TYPE_KEY, value.name)

    var selectedCollegeBuilding: CollegeBuilding
        get() {
            val id = settings.getString(SELECTED_COLLEGE_BUILDING_KEY, CollegeBuilding.First.id)

            return CollegeBuilding.entries.find {
                it.id == id
            }!!
        }
        set(value) = settings.set(SELECTED_COLLEGE_BUILDING_KEY, value.id)
}