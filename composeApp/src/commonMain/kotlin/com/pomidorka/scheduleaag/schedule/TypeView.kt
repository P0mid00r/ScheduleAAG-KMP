package com.pomidorka.scheduleaag.schedule

sealed class TypeView(val value: String) {
    data object New : TypeView("Новое")
    data object Old : TypeView("Старое")
}