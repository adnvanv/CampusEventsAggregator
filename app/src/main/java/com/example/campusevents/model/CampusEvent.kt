package com.example.campusevents.model

data class CampusEvent(
    val id: String,
    val title: String,
    val category: String,
    val location: String,
    val description: String,
    val organizer: String,
    val startTimeIso: String,
    val endTimeIso: String,
    val tags: List<String>
)
