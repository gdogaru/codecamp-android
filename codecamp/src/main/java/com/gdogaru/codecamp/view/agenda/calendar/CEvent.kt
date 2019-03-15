package com.gdogaru.codecamp.view.agenda.calendar


import org.threeten.bp.LocalTime

data class CEvent(
        val id: String,
        val start: LocalTime,
        val end: LocalTime,
        val preferedIdx: Int,
        val title: String,
        val descLine1: String,
        val descLine2: String
)
