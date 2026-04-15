package com.example.campusevents.ui.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campusevents.model.CampusEvent
import com.example.campusevents.ui.app.CampusEventsUiState
import com.example.campusevents.ui.components.EventCard
import com.example.campusevents.ui.preview.previewEvents
import com.example.campusevents.ui.theme.CampusEventsTheme

@Composable
fun SavedEventsScreen(
    uiState: CampusEventsUiState,
    onEventSelected: (String) -> Unit,
    onToggleSaved: (CampusEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Saved events",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        if (uiState.savedEvents.isEmpty()) {
            item {
                Card {
                    Text(
                        text = "Save an event from the feed to keep it here and schedule a reminder.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            items(uiState.savedEvents, key = { it.id }) { event ->
                EventCard(
                    event = event,
                    isSaved = true,
                    onClick = { onEventSelected(event.id) },
                    onToggleSaved = { onToggleSaved(event) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SavedEventsScreenPreview() {
    CampusEventsTheme {
        SavedEventsScreen(
            uiState = CampusEventsUiState(
                isLoading = false,
                allEvents = previewEvents,
                filteredEvents = previewEvents,
                savedEvents = previewEvents,
                savedEventIds = previewEvents.map { it.id }.toSet()
            ),
            onEventSelected = {},
            onToggleSaved = { _ -> }
        )
    }
}
