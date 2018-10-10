package com.coniferproductions.positions

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface PositionDao {
    @Query("SELECT * from position")
    fun getAllPositions(): LiveData<List<Position>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(position: Position)

    @Query("DELETE from position")
    fun deleteAll()
}