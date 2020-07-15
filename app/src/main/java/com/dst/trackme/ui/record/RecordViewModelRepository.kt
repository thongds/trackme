/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.ui.record

import com.dst.trackme.data.local.TrackMeDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecordViewModelRepository(val sessionId : Long, private val trackMeDao: TrackMeDao) {
    fun getSession() = trackMeDao.getSessionById(sessionId)
    fun getLastLatLn()  = trackMeDao.getLastLatLongOfSession(sessionId)
    suspend fun saveSession(duration : Int){
        withContext(Dispatchers.IO){
            val startLatLn = trackMeDao.getFirstLatLongOfSession(sessionId)
            val end = trackMeDao.getLastLatLongOfSessionRawValue(sessionId)
            if (startLatLn == null || end == null) return@withContext
            val sessionEntryResult = trackMeDao.getSessionByIdRawValue(sessionId)
            sessionEntryResult?.let {sessionEntry ->
                sessionEntry.startLat = startLatLn.lat
                sessionEntry.startLn = startLatLn.lng
                sessionEntry.endLat = end.lat
                sessionEntry.endLn = end.lng
                sessionEntry.duration = duration
                sessionEntry.valid = true
                trackMeDao.updateSession(sessionEntry)
            }
        }
    }
    suspend fun deleteSession(){
        withContext(Dispatchers.IO){
            trackMeDao.getSessionByIdRawValue(sessionId)?.let {sessionEntry->
                trackMeDao.deleteLatLnOfSession(sessionId)
                trackMeDao.deleteSession(sessionEntry)
            }
        }
    }
}