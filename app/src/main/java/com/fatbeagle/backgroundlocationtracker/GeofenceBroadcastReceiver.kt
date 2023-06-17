/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fatbeagle.backgroundlocationtracker

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent


class GeofenceBroadcastReceiver : BroadcastReceiver() {
    // ...
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Got something")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        Log.d(TAG, geofencingEvent.toString())
        if (geofencingEvent?.hasError() == true) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent?.geofenceTransition

        // Test that the reported transition was of interest.
        if ((geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL)) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent?.triggeringGeofences

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails(
                this,
                geofenceTransition!!,
                triggeringGeofences
            )

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails, context)
            Log.i("Geofence Broadcast", geofenceTransitionDetails)
        } else {
            // Log the error.
            Log.e(TAG, "Received GeoFenceTransition: " +
                geofenceTransition)
        }
    }

    fun openWhatsappContact(message: String, context: Context): PendingIntent {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
        sendIntent.type = "text/plain"

        return PendingIntent.getActivity(
            context,
            0,
            sendIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun sendNotification(geofenceTransitionDetails: String, context: Context) {

        val notification = NotificationCompat.Builder(context, "location")
            .setContentTitle("Notification of arrival")
            .setContentText("You have reached your target location :)")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .addAction(0, "Send", openWhatsappContact("I have reached", context))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "Notification permission is not given")
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(2, notification.build())
        Log.d(TAG, "I am in notification code")
    }


    private fun getGeofenceTransitionDetails(
        geofenceBroadcastReceiver: GeofenceBroadcastReceiver,
        geofenceTransition: Int,
        triggeringGeofences: List<Geofence>?
    ): String {
        var firstGeofence = triggeringGeofences?.first()
        return "$geofenceTransition - ${firstGeofence?.latitude} : ${firstGeofence?.longitude}"
    }
}