package com.example.campusevents.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campusevents.model.CampusEvent
import com.example.campusevents.ui.preview.previewEvents
import com.example.campusevents.ui.theme.CampusEventsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: CampusEvent,
    isFavorite: Boolean,
    isBookmarked: Boolean,
    onBack: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onBookmarkToggle: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                event.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                event.category,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text("Time: ${event.time}", style = MaterialTheme.typography.bodyLarge)
            Text("Location: ${event.location}", style = MaterialTheme.typography.bodyLarge)
            Text(event.description, style = MaterialTheme.typography.bodyMedium)

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = onFavoriteToggle) {
                    Text(if (isFavorite) "Favorited" else "Favorite")
                }
                OutlinedButton(onClick = onBookmarkToggle) {
                    Text(if (isBookmarked) "Bookmarked" else "Bookmark")
                }
            }

            Spacer(modifier = Modifier.size(8.dp))
            OutlinedButton(onClick = onBack) {
                Text("Back to Feed")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EventDetailScreenPreview() {
    CampusEventsTheme {
        EventDetailScreen(
            event = previewEvents.first(),
            isFavorite = false,
            isBookmarked = true,
            onBack = {},
            onFavoriteToggle = {},
            onBookmarkToggle = {}
        )
    }
}
