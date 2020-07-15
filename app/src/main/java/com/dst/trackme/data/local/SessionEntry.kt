/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.data.local

import androidx.room.*

@Entity(tableName = "session_table")
data class SessionEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var sessionId : Long = 0,
    @ColumnInfo(name = "duration")
    var duration : Int = 0,
    @ColumnInfo(name = "distance")
    var distance : Float = 0F,
    @ColumnInfo(name = "start_lat")
    var startLat : Double = 0.0,
    @ColumnInfo(name = "start_ln")
    var startLn : Double = 0.0,
    @ColumnInfo(name = "end_lat")
    var endLat : Double = 0.0,
    @ColumnInfo(name = "end_ln")
    var endLn : Double = 0.0,
    @ColumnInfo(name = "created_at")
    var createdTime : Long = System.currentTimeMillis(),
    @ColumnInfo(name = "valid")
    var valid : Boolean = false
)

@Entity(tableName = "lat_long_session_table",
    foreignKeys = [ForeignKey(entity = SessionEntry::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("session_id"))
    ],
    indices = [Index(value = ["session_id"])]
)
data class LatLongSessionEntry (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id : Long = 0L,
    @ColumnInfo(name = "lat")
    var lat : Double,
    @ColumnInfo(name = "lng")
    var lng : Double,
    @ColumnInfo(name = "created_at")
    var createdTime : Long = System.currentTimeMillis(),
    @ColumnInfo(name = "current_speed")
    var currentSpeed : Float,
    @ColumnInfo(name = "session_id")
    var sessionId : Long

)