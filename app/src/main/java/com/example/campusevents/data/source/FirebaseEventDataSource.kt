package com.example.campusevents.data.source

import com.example.campusevents.model.CampusEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseEventDataSource(
    private val firestore: FirebaseFirestore?
) : EventDataSource {

    override fun observeEvents(): Flow<List<CampusEvent>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = db.collection(EVENTS_COLLECTION)
            .orderBy(START_TIME_FIELD)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val events = snapshot?.documents.orEmpty().mapNotNull { document ->
                    val title = document.getString("title") ?: return@mapNotNull null
                    val category = document.getString("category") ?: return@mapNotNull null
                    val location = document.getString("location") ?: return@mapNotNull null
                    val description = document.getString("description") ?: ""
                    val organizer = document.getString("organizer") ?: "Campus Organization"
                    val startTimeIso = document.getString("startTimeIso") ?: return@mapNotNull null
                    val endTimeIso = document.getString("endTimeIso") ?: startTimeIso
                    val tags = document.get("tags") as? List<*> ?: emptyList<Any>()
                    CampusEvent(
                        id = document.id,
                        title = title,
                        category = category,
                        location = location,
                        description = description,
                        organizer = organizer,
                        startTimeIso = startTimeIso,
                        endTimeIso = endTimeIso,
                        tags = tags.mapNotNull { it as? String }
                    )
                }
                trySend(events)
            }

        awaitClose { registration.remove() }
    }

    private companion object {
        const val EVENTS_COLLECTION = "events"
        const val START_TIME_FIELD = "startTimeIso"
    }
}
