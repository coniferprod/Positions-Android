package com.coniferproductions.positions

import android.app.Application
import android.arch.lifecycle.LiveData
import android.util.Log
import org.jetbrains.anko.doAsync

class PositionRepository(application: Application) {
    private val positionDao: PositionDao
    private val listLiveData: LiveData<List<Position>>

    init {
        val db = PositionDatabase.getInstance(application)
        positionDao = db?.positionDao()!!
        listLiveData = positionDao.getAllPositions()
    }

    fun getAllPositions(): LiveData<List<Position>> {
        return listLiveData
    }

    fun insert(position: Position) {
        doAsync {
            positionDao.insert(position)
            Log.i("PositionRepository", "Inserted ${position.latitude}, ${position.longitude} into the database")
        }
    }
}
