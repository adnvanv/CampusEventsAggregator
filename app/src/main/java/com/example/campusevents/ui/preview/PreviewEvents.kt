package com.example.campusevents.ui.preview

import com.example.campusevents.model.CampusEvent

val previewEvents = listOf(
    CampusEvent(
        id = "preview-1",
        title = "ACM Weekly Build Night",
        category = "ACM Meetings",
        time = "Tuesday, 7:00 PM - 8:30 PM",
        location = "Swearingen 2A11",
        description = "Work on projects and prep for hackathons with ACM peers."
    ),
    CampusEvent(
        id = "preview-2",
        title = "Intramural Soccer Team Signup",
        category = "Intramural Sports",
        time = "Thursday, 6:00 PM - 7:00 PM",
        location = "Wellness Center",
        description = "Meet teammates and complete registration before the season opens."
    )
)
