package com.coniferproductions.positions

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "position")
data class Position(
        @PrimaryKey(autoGenerate = true)
        var uid: Int?,

        @ColumnInfo(name = "latitude")
        var latitude: Double,

        @ColumnInfo(name = "longitude")
        var longitude: Double,

        @ColumnInfo(name = "altitude")
        var altitude: Double,

        @ColumnInfo(name = "timestamp")
        var timestamp: Date,

        @ColumnInfo(name = "description")
        var description: String
){
    @Ignore constructor():this(null,0.0,0.0,0.0, Date(),"")
}

// Consider this for timestamps: https://medium.com/androiddevelopers/room-time-2b4cf9672b98
