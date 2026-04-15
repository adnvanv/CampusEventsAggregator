package com.example.campusevents.data.repository

import com.example.campusevents.data.source.EventDataSource
import com.example.campusevents.model.CampusEvent

class EventRepository(
    private val dataSource: EventDataSource
) {
    fun getEvents(): List<CampusEvent> = dataSource.getEvents()

    fun getCategories(events: List<CampusEvent>): List<String> {
        return listOf("All") + events.map { it.category }.distinct()
    }
}
