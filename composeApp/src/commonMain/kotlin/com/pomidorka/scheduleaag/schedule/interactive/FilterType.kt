package com.pomidorka.scheduleaag.schedule.interactive

enum class FilterType {
    Group, Prep, Aud;

    companion object {
        fun parse(typeName: String): FilterType {
            return FilterType.entries.find {
                it.name.equals(typeName, ignoreCase = true)
            } ?: throw IllegalArgumentException("Unknown type $typeName")
        }
    }
}