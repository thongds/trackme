/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dst.trackme.data.local.SessionEntry
import com.dst.trackme.data.local.TrackMeDao
import kotlinx.coroutines.launch

class HistoryViewModel(trackMeDao: TrackMeDao) : ViewModel() {
    private var offset = 0
    private val historyViewModelRepository = HistoryViewModelRepository(trackMeDao)
    private val sessions = ArrayList<SessionEntry>()
    private val _sessionsData = MutableLiveData<List<SessionEntry>>()
    var dataLoading = MutableLiveData(false)
    val sessionData : LiveData<List<SessionEntry>>
    get() = _sessionsData
    fun getMoreSessionPage(){
        viewModelScope.launch {
            dataLoading.value = true
            val newSessions = historyViewModelRepository.getSessionOffset(offset* LIMIT)
            sessions.addAll(newSessions)
            offset++
            _sessionsData.value = sessions
            dataLoading.value = false
        }
    }
    init {
        refreshSessionPage()
    }
    fun refreshSessionPage(){
        offset = 0
        sessions.clear()
        getMoreSessionPage()
    }

    suspend fun createNewSession() : Long{
       return historyViewModelRepository.createNewSession()
    }

}