package com.example.campusevents.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.campusevents.data.source.AssetEventDataSource
import com.example.campusevents.data.source.EventDataSource
import com.example.campusevents.model.CampusEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

private val Context.savedEventsDataStore by preferencesDataStore(name = "saved_events")

class EventRepository(
    private val context: Context,
    private val remoteDataSource: EventDataSource,
    private val fallbackDataSource: AssetEventDataSource,
    val isFirebaseConfigured: Boolean
) {

    fun observeEvents(): Flow<List<CampusEvent>> {
        val fallbackEvents = fallbackDataSource.loadEvents()
        if (!isFirebaseConfigured) {
            return flowOf(fallbackEvents.sortedBy { it.startTimeIso })
        }

        return remoteDataSource.observeEvents()
            .catch { emit(fallbackEvents) }
            .map { events -> events.sortedBy { it.startTimeIso } }
            .distinctUntilChanged()
    }

    fun observeSavedEventIds(): Flow<Set<String>> {
        return context.savedEventsDataStore.data
            .map { preferences -> preferences[SAVED_EVENT_IDS] ?: emptySet() }
            .distinctUntilChanged()
    }

    suspend fun updateSavedState(eventId: String, shouldSave: Boolean) {
        context.savedEventsDataStore.edit { preferences ->
            val currentIds = preferences[SAVED_EVENT_IDS].orEmpty().toMutableSet()
            if (shouldSave) {
                currentIds.add(eventId)
            } else {
                currentIds.remove(eventId)
            }
            preferences[SAVED_EVENT_IDS] = currentIds
        }
    }

    companion object {
        private val SAVED_EVENT_IDS = stringSetPreferencesKey("saved_event_ids")
    }
}
