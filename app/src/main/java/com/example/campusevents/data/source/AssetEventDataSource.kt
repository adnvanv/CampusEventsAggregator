package com.example.campusevents.data.source

import android.content.Context
import com.example.campusevents.model.CampusEvent
import org.json.JSONArray

class AssetEventDataSource(
    private val context: Context
) : EventDataSource {

    override fun getEvents(): List<CampusEvent> {
        return runCatching {
            val jsonText = context.assets.open("events.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonText)
            List(jsonArray.length()) { index ->
                val event = jsonArray.getJSONObject(index)
                CampusEvent(
                    id = event.getString("id"),
                    title = event.getString("title"),
                    category = event.getString("category"),
                    time = event.getString("time"),
                    location = event.getString("location"),
                    description = event.getString("description")
                )
            }
        }.getOrElse { emptyList() }
    }
}
