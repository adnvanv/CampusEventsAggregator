package com.example.campusevents.data.source

import android.content.Context
import com.example.campusevents.model.CampusEvent
import org.json.JSONArray
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AssetEventDataSource(
    private val context: Context
) : EventDataSource {

    override fun observeEvents(): Flow<List<CampusEvent>> = flowOf(loadEvents())

    fun loadEvents(): List<CampusEvent> {
        return runCatching {
            val jsonText = context.assets.open("events.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonText)
            List(jsonArray.length()) { index ->
                val event = jsonArray.getJSONObject(index)
                CampusEvent(
                    id = event.getString("id"),
                    title = event.getString("title"),
                    category = event.getString("category"),
                    location = event.getString("location"),
                    description = event.getString("description"),
                    organizer = event.getString("organizer"),
                    startTimeIso = event.getString("startTimeIso"),
                    endTimeIso = event.getString("endTimeIso"),
                    tags = buildList {
                        val tagsArray = event.optJSONArray("tags") ?: JSONArray()
                        for (tagIndex in 0 until tagsArray.length()) {
                            add(tagsArray.getString(tagIndex))
                        }
                    }
                )
            }
        }.getOrElse { emptyList() }
    }
}
