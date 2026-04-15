package com.example.campusevents.data.source

import com.example.campusevents.model.CampusEvent
import kotlinx.coroutines.flow.Flow

interface EventDataSource {
    fun observeEvents(): Flow<List<CampusEvent>>
}
