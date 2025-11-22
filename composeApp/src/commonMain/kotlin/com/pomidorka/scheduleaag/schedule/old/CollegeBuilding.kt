package com.pomidorka.scheduleaag.schedule.old

sealed class CollegeBuilding(val id: String, val name: String) {
    data object First : CollegeBuilding("1", "1 Корпус")
    data object Second : CollegeBuilding("2", "2 Корпус")
    data object Third : CollegeBuilding("3", "3 Корпус")
    data object OPC : CollegeBuilding("opc", "ОПЦ")

    override fun toString() = name

    companion object {
        val entries
            get() = listOf(First, Second, Third, OPC)
    }
}