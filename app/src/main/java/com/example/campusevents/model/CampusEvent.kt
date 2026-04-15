package com.example.campusevents.model

data class CampusEvent(
    val id: String,
    val title: String,
    val category: String,
    val time: String,
    val location: String,
    val description: String
)
