package com.example.campusevents.notifications

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.campusevents.model.CampusEvent
import com.example.campusevents.util.EventTimeFormatter
import java.time.Duration
import java.time.OffsetDateTime

class EventReminderScheduler(
    private val context: Context
) {

    fun scheduleReminder(event: CampusEvent) {
        val startTime = runCatching { OffsetDateTime.parse(event.startTimeIso) }.getOrNull() ?: return
        val reminderTime = calculateReminderTime(startTime)
        val delay = Duration.between(OffsetDateTime.now(), reminderTime)
        if (delay.isNegative || delay.isZero) return

        val request = OneTimeWorkRequestBuilder<EventReminderWorker>()
            .setInitialDelay(delay)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .setInputData(
                workDataOf(
                    EventReminderWorker.KEY_EVENT_ID to event.id,
                    EventReminderWorker.KEY_TITLE to event.title,
                    EventReminderWorker.KEY_LOCATION to event.location,
                    EventReminderWorker.KEY_TIME to EventTimeFormatter.formatRange(event)
                )
            )
            .addTag(workName(event.id))
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            workName(event.id),
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancelReminder(eventId: String) {
        WorkManager.getInstance(context).cancelUniqueWork(workName(eventId))
    }

    private fun calculateReminderTime(startTime: OffsetDateTime): OffsetDateTime {
        val twoHoursBefore = startTime.minusHours(2)
        val thirtyMinutesBefore = startTime.minusMinutes(30)
        val now = OffsetDateTime.now()
        return when {
            twoHoursBefore.isAfter(now) -> twoHoursBefore
            thirtyMinutesBefore.isAfter(now) -> thirtyMinutesBefore
            else -> startTime.minusMinutes(10)
        }
    }

    private fun workName(eventId: String): String = "event-reminder-$eventId"
}
