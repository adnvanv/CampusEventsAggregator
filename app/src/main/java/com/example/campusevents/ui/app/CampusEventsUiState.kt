package com.example.campusevents.ui.app

import com.example.campusevents.model.CampusEvent

data class CampusEventsUiState(
    val isLoading: Boolean = true,
    val allEvents: List<CampusEvent> = emptyList(),
    val filteredEvents: List<CampusEvent> = emptyList(),
    val savedEvents: List<CampusEvent> = emptyList(),
    val savedEventIds: Set<String> = emptySet(),
    val categories: List<String> = listOf("All"),
    val tags: List<String> = listOf("All"),
    val selectedCategory: String = "All",
    val selectedTag: String = "All",
    val usingFallbackData: Boolean = false
)
