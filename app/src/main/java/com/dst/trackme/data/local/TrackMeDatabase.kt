/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SessionEntry::class,LatLongSessionEntry::class], version = 1, exportSchema = false)
abstract class TrackMeDatabase : RoomDatabase() {

    abstract val trackMeDatabaseDao: TrackMeDao
    companion object {
        @Volatile
        private var INSTANCE: TrackMeDatabase? = null
        fun getInstance(context: Context): TrackMeDatabase {
            //prevent thread race condition
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TrackMeDatabase::class.java,
                        "trackme.db"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
