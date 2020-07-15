/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TrackMeDao {
    @Insert
    fun insertSession(sessionEntry: SessionEntry) : Long
    @Insert
    fun insertLatLng(latLongSessionEntry: LatLongSessionEntry)

    @Query("SELECT * FROM session_table ORDER BY created_at DESC ")
    fun getAllSession() : LiveData<List<SessionEntry>>

    @Query("SELECT * FROM lat_long_session_table WHERE session_id = :sessionID ORDER BY created_at DESC limit 1")
    fun getLastLatLongOfSession(sessionID : Long) : LiveData<LatLongSessionEntry>

    @Query("SELECT * FROM lat_long_session_table WHERE session_id = :sessionID ORDER BY created_at DESC limit 1")
    fun getLastLatLongOfSessionRawValue(sessionID : Long) : LatLongSessionEntry?

    @Query("SELECT * FROM lat_long_session_table WHERE session_id = :sessionID ORDER BY created_at ASC limit 1")
    fun getFirstLatLongOfSession(sessionID : Long) : LatLongSessionEntry?

    @Query("SELECT * FROM session_table WHERE id =:sessionID")
    fun getSessionById(sessionID: Long) : LiveData<SessionEntry>

    @Query("SELECT * FROM session_table WHERE id =:sessionID")
    suspend fun getSessionByIdRawValue(sessionID: Long) : SessionEntry?

    @Query("DELETE  FROM lat_long_session_table WHERE session_id = :sessionID ")
    fun deleteLatLnOfSession(sessionID: Long)

    @Query("SELECT * FROM session_table WHERE valid = :isValid  ORDER BY created_at DESC  Limit :limit OFFSET :offset")
    suspend fun getSessionPage(offset : Int,limit : Int,isValid : Boolean = true ) : List<SessionEntry>

    @Delete
    suspend fun deleteSession(sessionEntry: SessionEntry)
    @Update
    suspend fun updateSession(sessionEntry: SessionEntry)

}