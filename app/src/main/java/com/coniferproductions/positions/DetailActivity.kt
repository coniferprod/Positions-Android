package com.coniferproductions.positions

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import java.util.*

class DetailActivity: AppCompatActivity() {
    val TAG = "DetailActivity"

    var isDirty: Boolean = false // true if position not saved
    var position: Position? = null
    private var viewModel: PositionViewModel? = null

    private var positionCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail)
        setSupportActionBar(findViewById(R.id.detailToolbar))

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProviders.of(this).get(PositionViewModel::class.java)

        isDirty = true

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        if (sharedPref != null) {
            val defaultValue = positionCount
            positionCount = sharedPref.getInt(getString(R.string.position_count_key), defaultValue)
        }

        val description = "Position $positionCount"
        val descriptionEditText = findViewById<TextInputEditText>(R.id.detailDescriptionEditText)
        descriptionEditText.setText(description)

        val latitude = intent.getDoubleExtra(DETAIL_LATITUDE, 0.0)
        val longitude = intent.getDoubleExtra(DETAIL_LONGITUDE, 0.0)
        val altitude = intent.getDoubleExtra(DETAIL_ALTITUDE, 0.0)

        val latitudeEditText = findViewById<TextInputEditText>(R.id.detailLatitudeEditText)
        latitudeEditText.setText(latitude.toString())

        val longitudeEditText = findViewById<TextInputEditText>(R.id.detailLongitudeEditText)
        longitudeEditText.setText(longitude.toString())

        val altitudeEditText = findViewById<TextInputEditText>(R.id.detailAltitudeEditText)
        altitudeEditText.setText(altitude.toString())

        position = Position(null, latitude, longitude, altitude, timestamp = Date(), description = description)
    }

    override fun onStop() {
        super.onStop()

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putInt(getString(R.string.position_count_key), positionCount)
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            Log.i(TAG, "Save the position")
            isDirty = false
            val pos = this.position
            if (pos != null) {
                viewModel!!.insert(pos)
                isDirty = false
                positionCount += 1
                Log.i(TAG, "Saved '${pos.description}'")
            }
            else {
                Log.e(TAG, "Position is null, unable to insert to database")
            }

            onBackPressed()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}