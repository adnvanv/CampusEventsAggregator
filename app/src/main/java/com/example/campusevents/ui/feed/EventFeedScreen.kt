package com.example.campusevents.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campusevents.model.CampusEvent
import com.example.campusevents.ui.app.CampusEventsUiState
import com.example.campusevents.ui.app.CampusEventsViewModel
import com.example.campusevents.ui.components.EventCard
import com.example.campusevents.ui.preview.previewEvents
import com.example.campusevents.ui.theme.CampusEventsTheme

@Composable
fun EventFeedScreen(
    uiState: CampusEventsUiState,
    onCategorySelected: (String) -> Unit,
    onTagSelected: (String) -> Unit,
    onClearFilters: () -> Unit,
    onEventSelected: (String) -> Unit,
    onToggleSaved: (CampusEvent) -> Unit
) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .wrapContentSize()
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "One feed for campus life",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Browse club meetings, student events, and campus pop-ups in one place.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.usingFallbackData) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        }
                    )
                ) {
                    Text(
                        text = if (uiState.usingFallbackData) {
                            "Showing bundled sample events until Firebase is configured."
                        } else {
                            "Firebase sync is active for live campus events."
                        },
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            FilterSection(
                title = "Event type",
                filters = uiState.categories,
                selectedFilter = uiState.selectedCategory,
                onSelected = onCategorySelected
            )
        }

        item {
            FilterSection(
                title = "Tags",
                filters = uiState.tags,
                selectedFilter = uiState.selectedTag,
                onSelected = onTagSelected
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${uiState.filteredEvents.size} events match",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Button(onClick = onClearFilters) {
                    Text("Clear filters")
                }
            }
        }

        if (uiState.filteredEvents.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "No events match this combination yet. Try a different category or tag.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            items(uiState.filteredEvents, key = { it.id }) { event ->
                EventCard(
                    event = event,
                    isSaved = event.id in uiState.savedEventIds,
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

@Composable
private fun FilterSection(
    title: String,
    filters: List<String>,
    selectedFilter: String,
    onSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { onSelected(filter) },
                    label = { Text(filter) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EventFeedScreenPreview() {
    CampusEventsTheme {
        EventFeedScreen(
            uiState = CampusEventsUiState(
                isLoading = false,
                allEvents = previewEvents,
                filteredEvents = previewEvents,
                savedEventIds = setOf(previewEvents.first().id),
                savedEvents = listOf(previewEvents.first()),
                categories = listOf(CampusEventsViewModel.FILTER_ALL, "ACM Meetings", "Intramural Sports"),
                tags = listOf(CampusEventsViewModel.FILTER_ALL, "tech", "sports"),
                selectedCategory = CampusEventsViewModel.FILTER_ALL,
                selectedTag = CampusEventsViewModel.FILTER_ALL,
                usingFallbackData = true
            ),
            onCategorySelected = {},
            onTagSelected = {},
            onClearFilters = {},
            onEventSelected = {},
            onToggleSaved = { _ -> }
        )
    }
}
