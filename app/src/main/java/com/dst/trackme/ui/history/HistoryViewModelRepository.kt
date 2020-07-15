/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.ui.history

import com.dst.trackme.data.local.SessionEntry
import com.dst.trackme.data.local.TrackMeDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val LIMIT = 10
class HistoryViewModelRepository(private val trackMeDao: TrackMeDao) {

    suspend fun getSessionOffset(offset : Int) : List<SessionEntry>{
        return withContext(Dispatchers.IO){
            trackMeDao.getSessionPage(offset, LIMIT)
        }
    }
    suspend fun createNewSession() : Long {
        return  withContext(Dispatchers.IO){
            trackMeDao.insertSession(SessionEntry())
        }
    }
}