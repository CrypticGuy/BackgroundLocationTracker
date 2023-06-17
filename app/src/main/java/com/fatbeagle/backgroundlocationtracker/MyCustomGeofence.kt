package com.fatbeagle.backgroundlocationtracker

import android.location.Location
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest

class MyCustomGeofence {
    val geofenceList = mutableMapOf<String, Geofence>()

    private fun createGeofence(
        key: String,
        location: Location,
        radiusInMeters: Float,
        expirationTimeInMillis: Long,
    ): Geofence {
        return Geofence.Builder()
            .setRequestId(key)
            .setCircularRegion(location.latitude, location.longitude, radiusInMeters)
            .setExpirationDuration(expirationTimeInMillis)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
    }

    fun addGeofence(
        key: String,
        location: Location,
        radiusInMeters: Float = 100.0f,
        expirationTimeInMillis: Long = 30 * 60 * 1000,
    ) {
        geofenceList[key] = createGeofence(key, location, radiusInMeters, expirationTimeInMillis)
    }

    private fun createGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
            addGeofences(geofenceList.values.toList())
        }.build()
    }

}