package com.example.campusevents.ui.preview

import com.example.campusevents.model.CampusEvent

val previewEvents = listOf(
    CampusEvent(
        id = "preview-1",
        title = "ACM Weekly Build Night",
        category = "ACM Meetings",
        location = "Swearingen 2A11",
        description = "Work on projects and prep for hackathons with ACM peers.",
        organizer = "Association for Computing Machinery",
        startTimeIso = "2026-04-21T19:00:00-04:00",
        endTimeIso = "2026-04-21T20:30:00-04:00",
        tags = listOf("tech", "hackathon", "student org")
    ),
    CampusEvent(
        id = "preview-2",
        title = "Intramural Soccer Team Signup",
        category = "Intramural Sports",
        location = "Wellness Center",
        description = "Meet teammates and complete registration before the season opens.",
        organizer = "Campus Recreation",
        startTimeIso = "2026-04-24T18:00:00-04:00",
        endTimeIso = "2026-04-24T19:00:00-04:00",
        tags = listOf("sports", "fitness", "registration")
    )
)
