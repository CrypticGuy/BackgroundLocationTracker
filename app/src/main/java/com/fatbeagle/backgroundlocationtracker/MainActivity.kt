package com.fatbeagle.backgroundlocationtracker

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.mapmyindia.sdk.maps.MapView
import com.mapmyindia.sdk.maps.MapmyIndia
import com.mapmyindia.sdk.maps.MapmyIndiaMap
import com.mapmyindia.sdk.maps.OnMapReadyCallback
import com.mapmyindia.sdk.maps.annotations.Marker
import com.mapmyindia.sdk.maps.annotations.MarkerOptions
import com.mapmyindia.sdk.maps.annotations.PolygonOptions
import com.mapmyindia.sdk.maps.camera.CameraPosition
import com.mapmyindia.sdk.maps.geometry.LatLng
import com.mapmyindia.sdk.maps.location.LocationComponentActivationOptions
import com.mapmyindia.sdk.turf.TurfMeasurement.EARTH_RADIUS
import com.mmi.services.account.MapmyIndiaAccountManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.cos
import kotlin.math.sin

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "config")
class MainActivity : ComponentActivity(), OnMapReadyCallback {

    val LATITUDE_KEY = doublePreferencesKey("latitude")
    val LONGITUDE_KEY = doublePreferencesKey("longitude")
    private lateinit var mapView : MapView
    private lateinit var locationUpdates : TextView
    var REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private var mapmyIndiaMap: MapmyIndiaMap? = null
    private lateinit var longClickMarker: Marker
    private lateinit var polygon: com.mapmyindia.sdk.maps.annotations.Polygon
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
            ),
            0
        )
        MapmyIndiaAccountManager.getInstance().restAPIKey = getString(R.string.mapmyindia_rest_api_key)
        MapmyIndiaAccountManager.getInstance().mapSDKKey = getString(R.string.mapmyindia_rest_api_key)
        MapmyIndiaAccountManager.getInstance().atlasClientId = getString(R.string.mapmyindia_atlas_client_id)
        MapmyIndiaAccountManager.getInstance().atlasClientSecret = getString(R.string.mapmyindia_atlas_client_secret)
        MapmyIndia.getInstance(applicationContext)

        setContentView(R.layout.main_activity_layout)
        mapView = findViewById<MapView>(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                startService(this)
            }
        }

        val stopButton = findViewById<Button>(R.id.stopButton)
        stopButton.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }
        }
        locationUpdates = findViewById<TextView>(R.id.locationUpdates)

        val geofenceButton = findViewById<ExtendedFloatingActionButton>(R.id.set_geofence)
        geofenceButton.setOnClickListener {

            scope.launch {
                setLatitudeAndLongitude(20.0, 30.0)
            }
        }

    }

    suspend fun getLatitudeAndLongitude(): Pair<Double, Double> {
        val latitudeAndLongitudeString: Flow<Pair<Double, Double>> = dataStore.data
            .map { preferences ->
                // No type safety.
                Pair(preferences[LATITUDE_KEY]?:360.0, preferences[LONGITUDE_KEY]?:360.0)
            }
        return latitudeAndLongitudeString.first()
    }

    private suspend fun setLatitudeAndLongitude(latitude: Double, longitude: Double) {
        dataStore.edit { settings ->
            settings[LATITUDE_KEY] = latitude
            settings[LONGITUDE_KEY] = longitude
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LocationUpdateEvent?) {
        val latitude = event?.location?.latitude
        val longitude = event?.location?.longitude
        locationUpdates.text = "Latitude: $latitude Longitude: $longitude"
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        mapView.onStart()
    }

    public override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onMapReady(p0: MapmyIndiaMap) {
        Log.d(TAG, "Outside callback")
        this.mapmyIndiaMap = p0
        p0.getStyle {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return@getStyle
            }
            Log.d(TAG, "Inside callback")
            p0.locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(this@MainActivity, it).build())
            p0.locationComponent.isLocationComponentEnabled = true

        }

        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(28.8978, 77.3245))
            .zoom(8.0)
            .tilt(0.0)
            .build()

        p0.cameraPosition = cameraPosition

        p0.addOnMapLongClickListener(object: MapmyIndiaMap.OnMapLongClickListener {
            override fun onMapLongClick(latLng: LatLng): Boolean {
                val string: String = String.format("User clicked at: %s", latLng.toString())
                Log.d(TAG, string)
                if (this@MainActivity::longClickMarker.isInitialized) {
                    longClickMarker.remove()
                }
                if (this@MainActivity::polygon.isInitialized) {
                    p0.removePolygon(polygon)
                }
                val markerOptions: MarkerOptions = MarkerOptions().position(latLng)
                markerOptions.title= "Marker"
                markerOptions.snippet = "This is a Marker"
                longClickMarker = mapmyIndiaMap?.addMarker(markerOptions)!!
                val circleCoords = drawCircle(latLng, 500)
                var polygonOptions = PolygonOptions().addAll(circleCoords).alpha(0.4f).fillColor(Color.Black.hashCode())
                polygon = polygonOptions.polygon
                p0!!.addPolygon(polygonOptions)
                scope.launch {
                    setLatitudeAndLongitude(latLng.latitude, latLng.longitude)
                }
                return false
            }
        })

    }

    fun getPoint(center: LatLng, radius: Int, angle: Double): LatLng {
        val east: Double = radius * cos(angle);
        val north: Double = radius * sin(angle);

        val cLat = center.latitude;
        val cLng = center.longitude;
        val latRadius = EARTH_RADIUS * cos(cLat / 180 * Math.PI);

        val newLat = cLat + (north / EARTH_RADIUS / Math.PI * 180);
        val newLng = cLng + (east / latRadius / Math.PI * 180);

        return LatLng(newLat, newLng);
    }

    fun drawCircle(center: LatLng, radius: Int): ArrayList<LatLng> {
        val points: ArrayList<LatLng>  = ArrayList<LatLng>();
        val totalPoints = 30; // number of corners of the pseudo-circle
        var i = 0
        while(i < totalPoints) {
            points.add(getPoint(center, radius, i*2*Math.PI/totalPoints))
            i += 1
        }
        return points
    }

    override fun onMapError(p0: Int, p1: String?) {

    }

}