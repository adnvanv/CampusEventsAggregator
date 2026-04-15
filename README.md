# CampusEvents

CampusEvents is a Campus Event Aggregator app for Android. It shows a consolidated feed of campus events and club meetings, supports filtering by event type and tags, lets users save events, and schedules reminder notifications for saved events.

## What is implemented
- Multiple screens with Navigation Compose: feed, saved events, and event details
- `ViewModel`-driven UI state that survives rotation and normal lifecycle recreation
- Local persistence for saved events with `DataStore`
- Firebase Firestore event source with a bundled JSON fallback while Firebase is not configured
- Reminder notifications for saved events with `WorkManager`

## Open in Android Studio
1. Open Android Studio.
2. Select **Open** and choose this folder:
   `C:\Users\apvan\AndroidStudioProjects\CampusEvents`
3. Let Gradle Sync finish.

## Required SDK and tools
- Android SDK Platform `36`
- Android SDK Build-Tools
- Android SDK Platform-Tools
- Android Studio with Gradle JDK 17+
- Emulator or physical device running Android 7.0+ (`minSdk 24`)

## Run the app
1. Start an emulator or connect a device.
2. Select the `app` configuration.
3. Run the project.

The project compiles before Firebase is configured. Until Firebase is connected, the app uses the bundled sample feed from `app/src/main/assets/events.json`.

## Firebase setup
Use the official Android setup guide from Firebase:
- [Add Firebase to your Android project](https://firebase.google.com/docs/android/setup)
- [Get started with Cloud Firestore on Android](https://firebase.google.com/docs/firestore/quickstart)

### 1. Create a Firebase project
1. Go to the Firebase console.
2. Create a new project.
3. Add an Android app with package name `com.example.campusevents`.

### 2. Enable Firestore
1. In Firebase Console, open **Build > Firestore Database**.
2. Create the database in production mode or test mode.
3. Pick a region close to you.

### 3. Download `google-services.json`
1. Download the config file for the Android app you registered.
2. Open the file and copy these values into `app/src/main/res/values/firebase_config.xml`:
   `mobilesdk_app_id` -> `firebase_application_id`
   `current_key` -> `firebase_api_key`
   `project_info.project_id` -> `firebase_project_id`
   `project_info.project_number` -> `firebase_sender_id`
   `project_info.storage_bucket` -> `firebase_storage_bucket`

The app currently initializes Firebase from those resource strings so the project still builds even before you add the config.

### 4. Add Firestore event documents
Create a top-level collection named `events`.

Each document should look like this:

```json
{
  "title": "ACM Weekly Meeting",
  "category": "ACM Meetings",
  "location": "Swearingen 2A11",
  "description": "Join UofSC ACM for announcements and project check-ins.",
  "organizer": "Association for Computing Machinery",
  "startTimeIso": "2026-04-20T18:30:00-04:00",
  "endTimeIso": "2026-04-20T20:00:00-04:00",
  "tags": ["tech", "networking", "student org"]
}
```

Important:
- Store `startTimeIso` and `endTimeIso` as ISO-8601 strings.
- The app sorts Firestore events by `startTimeIso`.
- The Firestore document ID becomes the event ID used by saved events and reminders.

### 5. Firestore rules for class demo use
For a simple class project, these rules are enough while you demo locally:

```txt
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /events/{eventId} {
      allow read: if true;
      allow write: if true;
    }
  }
}
```

Lock these down if your instructor expects production-style security.

## Notifications
- On Android 13+, the app asks for notification permission when a user saves an event.
- Saved events automatically schedule a reminder before the event starts.

## Verify the build
From the project root:

```powershell
./gradlew.bat assembleDebug
```

## Notes
- Firebase removed the old `*-ktx` Android artifacts from the BoM in 2025, so this project uses `com.google.firebase:firebase-firestore` instead of `firebase-firestore-ktx`.
- If Gradle sync fails after adding Firebase values, re-sync the project and rebuild once.
