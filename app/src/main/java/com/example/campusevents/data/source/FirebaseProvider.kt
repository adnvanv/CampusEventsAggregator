package com.example.campusevents.data.source

import android.content.Context
import com.example.campusevents.R
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseProvider {

    fun provideFirestore(context: Context): FirebaseFirestore? {
        val applicationContext = context.applicationContext
        val app = FirebaseApp.getApps(applicationContext).firstOrNull()
            ?: initializeApp(applicationContext)
            ?: return null
        return runCatching { FirebaseFirestore.getInstance(app) }.getOrNull()
    }

    private fun initializeApp(context: Context): FirebaseApp? {
        val applicationId = context.getString(R.string.firebase_application_id)
        val apiKey = context.getString(R.string.firebase_api_key)
        val projectId = context.getString(R.string.firebase_project_id)
        val senderId = context.getString(R.string.firebase_sender_id)
        val storageBucket = context.getString(R.string.firebase_storage_bucket)

        if (applicationId.isBlank() || apiKey.isBlank() || projectId.isBlank() || senderId.isBlank()) {
            return null
        }

        val options = FirebaseOptions.Builder()
            .setApplicationId(applicationId)
            .setApiKey(apiKey)
            .setProjectId(projectId)
            .setGcmSenderId(senderId)
            .apply {
                if (storageBucket.isNotBlank()) {
                    setStorageBucket(storageBucket)
                }
            }
            .build()

        return runCatching { FirebaseApp.initializeApp(context, options) }.getOrNull()
    }
}
