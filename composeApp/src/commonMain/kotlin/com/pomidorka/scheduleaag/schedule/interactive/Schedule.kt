package com.pomidorka.scheduleaag.schedule.interactive

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val numberLesson: Int,
    val group: String,
    val lesson: String,
    val teacher: String,
    val territory: String,
    val classRoom: String,
)
