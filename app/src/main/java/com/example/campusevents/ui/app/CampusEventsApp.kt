package com.example.campusevents.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.campusevents.data.repository.EventRepository
import com.example.campusevents.data.source.AssetEventDataSource
import com.example.campusevents.ui.detail.EventDetailScreen
import com.example.campusevents.ui.feed.EventFeedScreen

@Composable
fun CampusEventsApp() {
    val context = LocalContext.current
    val repository = remember(context) {
        EventRepository(AssetEventDataSource(context.applicationContext))
    }

    val events = remember(repository) { repository.getEvents() }
    val categories = remember(events) { repository.getCategories(events) }

    val favorites = remember { mutableStateMapOf<String, Boolean>() }
    val bookmarks = remember { mutableStateMapOf<String, Boolean>() }

    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var selectedEventId by rememberSaveable { mutableStateOf<String?>(null) }

    val selectedEvent = remember(selectedEventId, events) {
        events.firstOrNull { it.id == selectedEventId }
    }

    if (selectedEvent == null) {
        EventFeedScreen(
            events = events,
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            onEventSelected = { selectedEventId = it }
        )
    } else {
        EventDetailScreen(
            event = selectedEvent,
            isFavorite = favorites[selectedEvent.id] == true,
            isBookmarked = bookmarks[selectedEvent.id] == true,
            onBack = { selectedEventId = null },
            onFavoriteToggle = { favorites[selectedEvent.id] = !(favorites[selectedEvent.id] == true) },
            onBookmarkToggle = { bookmarks[selectedEvent.id] = !(bookmarks[selectedEvent.id] == true) }
        )
    }
}
