package com.coniferproductions.positions

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.View
import android.widget.Toast
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class PositionsActivity : AppCompatActivity() {
    val TAG = "PositionsActivity"

    private var db: PositionDatabase? = null
    private lateinit var positionAdapter: PositionAdapter
    private var positions = mutableListOf<Position>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_positions)
        setSupportActionBar(findViewById(R.id.positionsToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val rv = findViewById<View>(R.id.positions) as RecyclerView
        positionAdapter = PositionAdapter(this, positions)
        rv.adapter = positionAdapter
        rv.layoutManager = LinearLayoutManager(this)

        db = PositionDatabase.getInstance(this)

        getPositionsFromDatabase()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_positions, menu)
        return true
    }

    private fun getPositionsFromDatabase() {
        doAsync {
            val data = db?.positionDao()?.getAll()

            uiThread {
                if (data == null || data.size == 0) {
                    showToast("No data in database!", Toast.LENGTH_SHORT)
                } else {
                    positions.clear()
                    positions.addAll(data)
                    positionAdapter.notifyDataSetChanged()
                }
            }
        }
    }

}

