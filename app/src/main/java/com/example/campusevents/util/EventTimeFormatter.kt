package com.example.campusevents.util

import com.example.campusevents.model.CampusEvent
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object EventTimeFormatter {

    private val dayFormatter = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US)
    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
    private val detailFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d 'at' h:mm a", Locale.US)

    fun formatRange(event: CampusEvent): String {
        val start = parse(event.startTimeIso) ?: return "Time TBD"
        val end = parse(event.endTimeIso) ?: return detailFormatter.format(start)
        val day = dayFormatter.format(start)
        return "$day • ${timeFormatter.format(start)} - ${timeFormatter.format(end)}"
    }

    fun formatDetailStart(event: CampusEvent): String {
        val start = parse(event.startTimeIso) ?: return "Time TBD"
        return detailFormatter.format(start)
    }

    fun formatReminderWindow(event: CampusEvent): String {
        val start = parse(event.startTimeIso) ?: return "Reminder unavailable"
        return when {
            start.isAfter(OffsetDateTime.now().plusHours(2)) -> "Reminder set for 2 hours before start"
            start.isAfter(OffsetDateTime.now().plusMinutes(30)) -> "Reminder set for 30 minutes before start"
            else -> "Reminder set for 10 minutes before start"
        }
    }

    private fun parse(value: String): OffsetDateTime? = runCatching { OffsetDateTime.parse(value) }.getOrNull()
}
