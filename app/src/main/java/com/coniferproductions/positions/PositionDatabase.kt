package com.coniferproductions.positions

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

@Database(entities = arrayOf(Position::class), version = 1)
@TypeConverters(MyTypeConverters::class)
abstract class PositionDatabase : RoomDatabase() {

    abstract fun positionDao(): PositionDao

    companion object {
        private var INSTANCE: PositionDatabase? = null

        fun getInstance(context: Context): PositionDatabase? {
            if (INSTANCE == null) {
                synchronized(PositionDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PositionDatabase::class.java, "positions.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}