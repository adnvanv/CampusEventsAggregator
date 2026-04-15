package com.example.campusevents.ui.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.campusevents.data.repository.EventRepository
import com.example.campusevents.data.source.AssetEventDataSource
import com.example.campusevents.data.source.FirebaseEventDataSource
import com.example.campusevents.data.source.FirebaseProvider
import com.example.campusevents.model.CampusEvent
import com.example.campusevents.notifications.EventReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CampusEventsViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val appContext = application.applicationContext
    private val firestore = FirebaseProvider.provideFirestore(appContext)
    private val repository = EventRepository(
        context = appContext,
        remoteDataSource = FirebaseEventDataSource(firestore),
        fallbackDataSource = AssetEventDataSource(appContext),
        isFirebaseConfigured = firestore != null
    )
    private val reminderScheduler = EventReminderScheduler(appContext)

    private val selectedCategory = savedStateHandle.getStateFlow(KEY_CATEGORY, FILTER_ALL)
    private val selectedTag = savedStateHandle.getStateFlow(KEY_TAG, FILTER_ALL)

    val uiState: StateFlow<CampusEventsUiState> = combine(
        repository.observeEvents(),
        repository.observeSavedEventIds(),
        selectedCategory,
        selectedTag
    ) { events, savedEventIds, category, tag ->
        val categories = listOf(FILTER_ALL) + events.map { it.category }.distinct()
        val tags = listOf(FILTER_ALL) + events.flatMap { it.tags }.distinct().sorted()
        val filteredEvents = events.filter { event ->
            matchesCategory(event, category) && matchesTag(event, tag)
        }
        CampusEventsUiState(
            isLoading = false,
            allEvents = events,
            filteredEvents = filteredEvents,
            savedEvents = events.filter { it.id in savedEventIds },
            savedEventIds = savedEventIds,
            categories = categories,
            tags = tags,
            selectedCategory = category,
            selectedTag = tag,
            usingFallbackData = !repository.isFirebaseConfigured
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CampusEventsUiState()
    )

    fun selectCategory(category: String) {
        savedStateHandle[KEY_CATEGORY] = category
    }

    fun selectTag(tag: String) {
        savedStateHandle[KEY_TAG] = tag
    }

    fun clearFilters() {
        savedStateHandle[KEY_CATEGORY] = FILTER_ALL
        savedStateHandle[KEY_TAG] = FILTER_ALL
    }

    fun toggleSaved(event: CampusEvent) {
        val shouldSave = event.id !in uiState.value.savedEventIds
        viewModelScope.launch {
            repository.updateSavedState(event.id, shouldSave)
            if (shouldSave) {
                reminderScheduler.scheduleReminder(event)
            } else {
                reminderScheduler.cancelReminder(event.id)
            }
        }
    }

    private fun matchesCategory(event: CampusEvent, category: String): Boolean {
        return category == FILTER_ALL || event.category == category
    }

    private fun matchesTag(event: CampusEvent, tag: String): Boolean {
        return tag == FILTER_ALL || event.tags.any { it.equals(tag, ignoreCase = true) }
    }

    companion object {
        private const val KEY_CATEGORY = "selected_category"
        private const val KEY_TAG = "selected_tag"
        const val FILTER_ALL = "All"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                CampusEventsViewModel(
                    application = application,
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }
    }
}
