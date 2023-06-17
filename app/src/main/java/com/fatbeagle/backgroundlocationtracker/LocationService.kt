package com.fatbeagle.backgroundlocationtracker

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.NotificationCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber

class LocationService: Service() {

    val LATITUDE_KEY = doublePreferencesKey("latitude")
    val LONGITUDE_KEY = doublePreferencesKey("longitude")

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mainScope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var locationClient: LocationClient
    private lateinit var geofenceClient: GeofencingClient
    private val TAG = "GeofenceManager"
    val geofenceList = mutableMapOf<String, Geofence>()
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        geofenceClient = LocationServices.getGeofencingClient(applicationContext)
    }

    suspend fun getLatitudeAndLongitude(): Pair<Double, Double> {
        val latitudeAndLongitudeString: Flow<Pair<Double, Double>> = dataStore.data
            .map { preferences ->
                // No type safety.
                Pair(preferences[LATITUDE_KEY]?:360.0, preferences[LONGITUDE_KEY]?:360.0)
            }
        return latitudeAndLongitudeString.first()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        var location: Location = Location("")
        val job = mainScope.launch {
                val loc = getLatitudeAndLongitude()

            Log.d(TAG, "LOC OBJ: ${loc.first} - ${loc.second}")
            location.latitude = loc.first
            location.longitude = loc.second
        }
        job.invokeOnCompletion {
            if (location.latitude > 350 || location.longitude > 350) {
                Toast.makeText(applicationContext, "Error in selecting longitude and latitude", Toast.LENGTH_LONG).show()
                return@invokeOnCompletion
            }
            Log.d(TAG, "${location.latitude} - ${location.longitude}")
            geofenceList["FirstGeofence"] = createGeofence(key="FirstGeofence", expirationTimeInMillis = 3600*1000, radiusInMeters = 500f, location = location)

            val notification = NotificationCompat.Builder(this, "location")
                .setContentTitle("Tracking location...")
                .setContentText("Location: null")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setOngoing(true)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            locationClient
                .getLocationUpdates(10000L)
                .catch { e -> e.printStackTrace() }
                .onEach { location ->
                    latitutde = location.latitude.toFloat()
                    longitude = location.longitude.toFloat()
                    val lat = latitutde.toString()
                    val long = longitude.toString()
                    val updateNotification = notification.setContentText(
                        "Location: ($lat, $long)"
                    )
                    notificationManager.notify(1, updateNotification.build())
                }
                .launchIn(serviceScope)

            registerGeofence()
            Toast.makeText(applicationContext, "Geofence registered", Toast.LENGTH_LONG).show()
            startForeground(1, notification.build())
        }
//        location.longitude = geoFenceLat
//        location.latitude = geoFenceLong

    }

    private fun stop() {
        stopForeground(true)
        runBlocking {
            deregisterGeofence()
        }
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP  = "ACTION_STOP"
        var latitutde = 0f
        var longitude = 0f
    }

    @SuppressLint("MissingPermission")
    fun registerGeofence() {

        geofenceClient.addGeofences(createGeofencingRequest(), geofencePendingIntent)
            .addOnSuccessListener {
                Log.d(TAG, "registerGeofence: SUCCESS")
                Log.d(TAG, geofenceClient.toString())
            }.addOnFailureListener { exception ->
                Log.d(TAG, "registerGeofence: Failure\n${exception.printStackTrace()}")
            }
        Timber.d("GeoFence register done")
    }

    suspend fun deregisterGeofence() = kotlin.runCatching {
        geofenceClient.removeGeofences(geofencePendingIntent).addOnSuccessListener{
            Log.d(TAG, "de-registerGeofence: SUCCESS")
        }.addOnFailureListener{ exception ->
            Log.d(TAG, "de-registerGeofence: Failure\n${exception.printStackTrace()}")
        }
            .await()
        geofenceList.clear()
    }

    private fun createGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
            addGeofences(geofenceList.values.toList())
        }.build()
    }

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
            .setLoiteringDelay(5000)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL)
            .build()
    }

}