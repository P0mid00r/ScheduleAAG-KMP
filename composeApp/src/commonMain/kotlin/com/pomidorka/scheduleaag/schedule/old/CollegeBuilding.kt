package com.pomidorka.scheduleaag.schedule.old

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope

sealed class CollegeBuilding(val id: String, val name: String) {
    data object First : CollegeBuilding("1", "1 Корпус")
    data object Second : CollegeBuilding("2", "2 Корпус")
    data object Third : CollegeBuilding("3", "3 Корпус")
    data object OPC : CollegeBuilding("opc", "ОПЦ")

    override fun toString() = name

    companion object {
        val entries
            get() = listOf(First, Second, Third, OPC)

        val Saver = object : Saver<MutableState<CollegeBuilding>, Int> {
            override fun SaverScope.save(value: MutableState<CollegeBuilding>): Int {
                return entries.indexOf(value.value)
            }

            override fun restore(value: Int): MutableState<CollegeBuilding> {
                return mutableStateOf(entries[value])
            }
        }
    }
}