package com.coniferproductions.positions

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MenuInflater
import java.util.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class DetailActivity: AppCompatActivity() {
    val TAG = "DetailActivity"

    var isDirty: Boolean = false // true if position not saved
    var position: Position? = null
    private var db: PositionDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail)
        setSupportActionBar(findViewById(R.id.detailToolbar))

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isDirty = true

        val latitude = intent.getDoubleExtra(DETAIL_LATITUDE, 0.0)
        val longitude = intent.getDoubleExtra(DETAIL_LONGITUDE, 0.0)
        val altitude = intent.getDoubleExtra(DETAIL_ALTITUDE, 0.0)

        val latitudeEditText = findViewById<TextInputEditText>(R.id.detailLatitudeEditText)
        latitudeEditText.setText(latitude.toString())

        val longitudeEditText = findViewById<TextInputEditText>(R.id.detailLongitudeEditText)
        longitudeEditText.setText(longitude.toString())

        val altitudeEditText = findViewById<TextInputEditText>(R.id.detailAltitudeEditText)
        altitudeEditText.setText(altitude.toString())

        position = Position(null, latitude, longitude, altitude, timestamp = Date(), description = "Position 1")

        db = PositionDatabase.getInstance(this)
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
            doAsync {
                db?.positionDao()?.insert(pos!!)
                Log.i(TAG, "Inserted ${pos?.latitude}, ${pos?.longitude} into the database")
                isDirty = false
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