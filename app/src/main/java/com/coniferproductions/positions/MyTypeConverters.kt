package com.coniferproductions.positions

import android.arch.persistence.room.TypeConverter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

object MyTypeConverters {
    val TIMESTAMP_FORMAT = "yyyy-MM-dd'T'hh:mm:ss'Z'"

    val timestampFormatter = SimpleDateFormat(TIMESTAMP_FORMAT)

    @TypeConverter
    @JvmStatic
    fun dateFromTimestamp(value: String?): Date? {
        if (value != null) {
            try {
                return timestampFormatter.parse(value)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return null
        } else {
            return null
        }
    }

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date?): String? {
        return timestampFormatter.format(date)
    }

}
