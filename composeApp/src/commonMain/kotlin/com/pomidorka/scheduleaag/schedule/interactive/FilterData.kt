package com.pomidorka.scheduleaag.schedule.interactive

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.listSaver
import kotlinx.serialization.Serializable

@Stable
@Immutable
@Serializable
data class FilterData(
    val data: String,
    val hash: String,
)

//val FilterDataSaver = listSaver<FilterData?, Any>(
//    save = {
//        if (it == null) emptyList()
//        else listOf(it.data, it.hash)
//    },
//    restore = {
//        if (it.isEmpty()) null
//        else {
//            FilterData(
//                data = it[0] as String,
//                hash = it[1] as String
//            )
//        }
//    }
//)
