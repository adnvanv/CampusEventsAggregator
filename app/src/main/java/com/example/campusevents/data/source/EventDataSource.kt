package com.example.campusevents.data.source

import com.example.campusevents.model.CampusEvent

interface EventDataSource {
    fun getEvents(): List<CampusEvent>
}
