package com.coniferproductions.positions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import android.support.design.widget.FloatingActionButton
import android.view.Menu
import android.view.MenuItem


// User location code derived from https://developer.android.com/training/location/retrieve-current

const val DETAIL_LATITUDE = "latitude"
const val DETAIL_LONGITUDE = "longitude"
const val DETAIL_ALTITUDE = "altitude"
const val DETAIL_DESCRIPTION = "description"

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
    private lateinit var locationCallback: LocationCallback

    // Keys for storing activity state in the Bundle.
    private val REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private val REQUEST_CHECK_SETTINGS = 0x1

    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000L
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

    private var requestingLocationUpdates: Boolean = false

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationSettingsRequest: LocationSettingsRequest

    private var latestLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.mainToolbar))

        requestingLocationUpdates = true
        updateValuesFromBundle(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        settingsClient = LocationServices.getSettingsClient(this)

        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            if (latestLocation != null) {
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra(DETAIL_LATITUDE, latestLocation!!.latitude)
                    putExtra(DETAIL_LONGITUDE, latestLocation!!.longitude)
                    putExtra(DETAIL_ALTITUDE, latestLocation!!.altitude)
                }
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_list -> {
            Log.i(TAG, "Show all positions")
            val intent = Intent(this, PositionsActivity::class.java)
            startActivity(intent)

            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        PositionDatabase.destroyInstance()

        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

        Log.i(TAG, "onResume")

        if (requestingLocationUpdates && checkPermissions()) {
            startLocationUpdates()
        }
        else if (!checkPermissions()) {
            requestPermissions()
        }

        updateUI()
    }

    override fun onPause() {
        super.onPause()

        stopLocationUpdates()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }

    fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                locationResult ?: return

                val location = locationResult.lastLocation
                Log.i(TAG, "Got location ${location.latitude}, ${location.longitude}")
                val latitudeEditText = findViewById<TextInputEditText>(R.id.latitudeEditText)
                latitudeEditText.setText("${location.latitude}")

                val longitudeEditText = findViewById<TextInputEditText>(R.id.longitudeEditText)
                longitudeEditText.setText("${location.longitude}")

                val altitudeEditText = findViewById<TextInputEditText>(R.id.altitudeEditText)
                altitudeEditText.setText("${location.altitude}")

                latestLocation = location
            }
        }
    }

    fun createLocationRequest() {
        locationRequest = LocationRequest().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        builder.build()
    }

    private fun startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val task = settingsClient.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            Log.i(TAG, "All location settings are satisfied.");

            //noinspection MissingPermission
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback, Looper.myLooper())

            updateUI()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this@MainActivity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
            }
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY)
        }

        // ...

        // Update UI to match restored state
        //updateUI()
    }

    private fun updateUI() {
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i("MainActivity", "Displaying permission rationale to provide additional context.")
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, View.OnClickListener {
                // Request permission
                ActivityCompat.requestPermissions(this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE)
            })
        } else {
            Log.i("MainActivity", "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun showSnackbar(mainTextStringId: Int, actionStringId: Int,
                             listener: View.OnClickListener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
}

fun Context.showToast(message: String, length: Int) {
    Toast.makeText(this, message, length)
            .show()
}
